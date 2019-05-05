import java.util.*;
// Class template for Determinant object and functions for manipulating an Determinant object
class Determinant{
	private final int order;	// order of determinant(length)
	private double[][] det;		// elements of the determinant
	private String value;		// value of determinant

	// Make a *blank* determinant of order = n, then input elements of determinant from user.
	// @Param int n - the order of the determinant
	Determinant(int n)throws ZeroOrderException{
		if(n<1) throw new ZeroOrderException();
		order = n;
		det = new double[n][n];
		value = null;
		System.out.print("Enter elements of Determinant:\n");
		input();
	}

	// Make a determinant from the given array as elements ( array may not be square )
	// @Param double[][] data - the elements of the determinant in an 2D array
	// Throws - ZeroOrderException - when passed array is null
	// 		  - MatrixNotSquareException - when passed array has inequal no of rows and columns
	Determinant(double[][] data)throws ZeroOrderException, MatrixNotSquareException {
		if(data.length<1)	throw new ZeroOrderException();
		if (data.length != data[0].length) throw new MatrixNotSquareException();
		det = data;
		order = det.length;
	}

	// Input elements of determinant from user into the determinant
	// Throws - ZeroOrderException - when current determinant has order = 0
	void input(){
		Scanner sc = new Scanner(System.in);
		for(int i = 0; i < order; i++){
			for(int j = 0; j < order; j++){
				det[i][j] = sc.nextDouble();
			}
		}
	}

	// Calculates the value of the current determinant by simple expansion
	// Throws - ZeroOrderException - if Determinant is of order 0
	// 		  - MatrixNotSquareException - if determinant is not square
	double getValue() throws ZeroOrderException, MatrixNotSquareException{
		if(value != null)
			return Double.parseDouble(value);
		// else calculate
		if(this.det.length == 1) return det[0][0];	// if 1x1 determinant , return the one and only element
		// else:
		int i = 0;	// expand using first row
		double val = 0;
		for(int j = 0 ; j < order; j++){
			
			val += det[i][j] * getCofacor(i,j);	// val += a.A
		}
		value = val+"";
		return val;
	}

	// Returns the minor of an element in the determinant
	// Throws - ZeroOrderException - by Determinant()
	// 		  - MatrixNotSquareException - by Determinant()
	// @Param int i, int j - index of element
	Determinant getMinor(int i_, int j_) throws ZeroOrderException, MatrixNotSquareException{
		double val = 0;
		double[][] min = new double[order - 1][order - 1];
		int k = 0;
		for(int i = 0 ; i < order; i++){
			for(int j = 0; j < order; j++){
				if(i==i_ || j == j_)	continue;
				double term = det[i][j];
				int i__ = k/(order-1);
				int j__ = k%(order-1);
				min[i__][j__] = term;
				k++;
			}
		}
		Determinant rv = new Determinant(min);
		return rv;
	}
	// Gets the value of the cofactor of an element in determinant
	// @Param int i, int j - index of element
	double getCofacor(int i,int j) throws ZeroOrderException, MatrixNotSquareException{
		int sign = ((i+j)%2 == 0)?1:-1;
		return  getMinor(i,j).getValue() * sign;
		// gets the value of minor of that element, then appropiates the sign
	}
	// Returns the adjugate determinant of the current determinant (A B C) for (a b c)
	Determinant getAdjugate() throws ZeroOrderException, MatrixNotSquareException{
		double[][] rv = new double[order][order];
		for(int i = 0; i < order; i++){
			for(int j = 0; j < order; j++){
				rv[i][j] = getCofacor(i,j);
			}
		}
		Determinant adj = new Determinant(rv);
		return adj;
	}

	// Displays the determinant on console.
	// Sides of determinant is boxed by '|'
	void display(){
		for(int i = 0; i < order; i++){
			System.out.print("|\t");
			for( int j = 0 ; j < order; j++){
				String value = det[i][j]+"";
				String decimal = value.substring(value.indexOf(".")+1);	// gets the fractional part
				if(isduck(decimal))										// if it is 0
					value = value.substring(0,value.indexOf("."));		// then it is not printed
				System.out.print(value + "\t");
			}
			System.out.println("|");
		}
	}

	// checks if a passed string is only consisted of '0'.
	private boolean isduck(String a){
		for(char each : a.toCharArray()){
			if(each != '0') return false;
		}
		return true;
	}

	// Adds two determinant and returns resultant determinant
	// @Param other - the Determinant to add to self.
	// Throws - UnaddableDeterminantException - when the two determinants are unaddable 
	// 								(different order or doesn't have *order-1* Rows/Columns common)
	// 		  - ZeroOrderException, MatrixNotSquareException - by Determinant().
	public Determinant add(Determinant other) throws UnaddableDeterminantException, ZeroOrderException, MatrixNotSquareException{
		// 0. check if of same order
		if(other.order != this.order){
			throw new UnaddableDeterminantException("Determinants are of different order");
		}
		// 1. check if *order-1* Rows/Columns are same
		int alikeR = 0, alikeC = 0;
		for(int i = 0; i < order; i++){
			if(compareRow(this,other,i)){
				alikeR++;
			}
			if(compareColumn(this,other,i)){
				alikeC++;
			}
		}
		if(alikeR != order - 1 && alikeC != order-1){
			throw new UnaddableDeterminantException(order - 1 + " rows/columns need to be identical");
		}
		// else: add them
		double[][] arr = new double[order][order];
		if(alikeC == order-1){		// add via columns 
			for(int i = 0; i < order; i++){
				if(compareColumn(this,other,i)){	// if this column is common, store it as is
					for(int j = 0; j < order; j++){
						arr[j][i] = this.det[j][i];
					}
				}
				else{								// else sum both columns and store in result
					for(int j = 0; j < order; j++){
						arr[j][i] = this.det[j][i] + other.det[j][i];
					}
				}
			}
		}
		else if(alikeR == order-1){		// add via rows
			for(int i = 0; i < order; i++){
				if(compareRow(this,other,i)){		// if this row is common, store it as is
					for(int j = 0; j < order; j++){
						arr[i][j] = this.det[i][j];
					}
				}
				else{								// else sum both rows and store in result
					for(int j = 0; j < order; j++){
						arr[i][j] = this.det[i][j] + other.det[i][j];
					}
				}
			}
		}
		return new Determinant(arr);
	}

	// Compares one row of two determinants, if they are same, returns true, else false
	// @Param a - one Determinant
	// 		  b - another Determinant
	// 		  i (int) - the row (0 based) to be compared
	private boolean compareRow(Determinant a, Determinant b, int i){
		boolean isIdentical = true;
		for(int j=0;j<order;j++){
			if (a.det[i][j]!=b.det[i][j]){
				isIdentical = false;
				break;
			}
		}
		return isIdentical;
	}

	// Compares one column of two determinants, if they are same, returns true, else false
	// @Param a - one Determinant
	// 		  b - another Determinant
	// 		  i (int) - the column (0 based) to be compared
	private boolean compareColumn(Determinant a, Determinant b, int i){
		boolean isIdentical = true;
		for(int j=0;j<order;j++){
			if (a.det[j][i]!=b.det[j][i]){
				isIdentical = false;
				break;
			}
		}
		return isIdentical;
	}

	public static void main(String[] args){
		try{
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter order of Determinant: ");
			int o = sc.nextInt();
			Determinant d = new Determinant(o);
			// values are inputted ^
			while(true){
				System.out.println("Determinant: ");
				d.display();
				System.out.println("Value of Determinant : " + d.getValue());

				System.out.println("What do you want to do?:\n"+
					"1. Cofactor of certain element\n"+
					"2. Minor of certain element\n"+
					"3. Adjugate of Determinant\n"+
					"4. Add with another Determinant");
				int ch = sc.nextInt();
				switch(ch){
					case 1:
						System.out.print("Enter i,j of the element whose cofactor you want: ");
						int i = sc.nextInt(); int j = sc.nextInt();
						Determinant cof = d.getMinor(i,j);
						int sign = (((i+j)%2==0)?1:-1);
						System.out.print("Cofactor: \n"+(sign == 1 ?"+":"-")+ " \n");
						cof.display();
						System.out.println("\nValue of cofactor: " + d.getCofacor(i,j));
						break;
					case 2:
						System.out.print("Enter i,j of the element whose minor you want: ");
						int i_ = sc.nextInt(); int j_ = sc.nextInt();
						Determinant minor = d.getMinor(i_,j_);
						System.out.print("Minor: \n");
						minor.display();
						System.out.println("\nValue of minor: " + minor.getValue());
						break;
					case 3:
						Determinant adj = d.getAdjugate();
						System.out.print("Adjugate: \n");
						adj.display();
						System.out.println("\nValue of Adjugate: " + adj.getValue());
						break;
					case 4:
						System.out.println("Another determinant should also be of order "+ d.order);
						Determinant b = new Determinant(d.order);
						System.out.print("Second Determinant: \n");		b.display();
						System.out.print("Value of Second Determinant: "+b.getValue());
						Determinant res = d.add(b);
						System.out.print("\nResultant Determinant: \n");	res.display();
						System.out.println("Value of Resultant Determinant: "+res.getValue());
					default: System.exit(0);
				}
			}
		}
		catch(ZeroOrderException zoe){
			System.err.println("Error: Determinant of size 0.");
			zoe.printStackTrace();
			System.exit(1);
		}
		catch(MatrixNotSquareException mnse){
			System.err.println("Error: Determinant is not square.");
			mnse.printStackTrace();
			System.exit(1);
		}
		catch(UnaddableDeterminantException ude){
			System.err.println("Error: Determinant is not addable.\n"+ude.getMessage());
			ude.printStackTrace();
			System.exit(1);
		}
	}
}