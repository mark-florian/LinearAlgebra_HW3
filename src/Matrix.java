import java.util.*;

public class Matrix 
{
	private int numRows;
	private int numCols;
	private double[][] matrix;
	private double[][] row;
	private double[][] col;
	private double[] solution;
	private Matrix GJ;
	private Matrix RREF;
	private Matrix eMatrix;
	public ArrayList<Integer> pivcol;
	public ArrayList<Integer> freecol;
	private int rank;
	
	public Matrix(int r, int c)
	{
		numRows = r;
		numCols = c;
		matrix = new double[r][c];
		row = new double[r][c];
		col = new double[c][r];
		solution = new double[c];
		pivcol = new ArrayList<Integer>();
		freecol = new ArrayList<Integer>();
	}
	
	public void addRow(double[] newRow, int currCol)
	{
		for(int i=0; i<numCols; i++)
			matrix[currCol][i] = newRow[i];
	}
	public double[] getSolution()
	{
		return solution;
	}
	public ArrayList<Integer> getFreeCols()
	{
		return freecol;
	}
	
	public void adjustRowCol()
	{
		for(int i=0; i<numRows; i++)
			for(int j=0; j<numCols; j++)
				row[i][j] = matrix[i][j];
		for(int i=0; i<numCols; i++)
			for(int j=0; j<numRows; j++)
				col[i][j] = matrix[j][i];
	}
	
	public void print()
	{
		for(int i=0; i<numRows; i++)
		{
			for(int j=0; j<numCols; j++)
				System.out.printf("%3.1f  ", matrix[i][j]);
			System.out.print(" |  " + solution[i]);
			System.out.println();
		}
	}

	public void printElimination()
	{
		eMatrix.print();
	}
	
	public void square()
	{
		for(int i=0; i<numRows; i++)
			for(int j=0; j<numCols; j++)
				matrix[i][j] = sum(row[i], col[j]);
		adjustRowCol();
	}
	
	private double sum(double[] r, double[] c)
	{
		double sum = 0;
		for(int i=0; i<r.length; i++)
			sum += (r[i] * c[i]);
		return sum;
	}
	
	public int getRowSize()
	{
		return numRows;
	}
	public int getColSize()
	{
		return numCols;
	}
	public double getElement(int r, int c)
	{
		return matrix[r][c];
	}
	public int getRank()
	{
		return rank;
	}
	
	public void setSolution(double[] d)
	{
		solution = d;
	}
	
	public void copy(Matrix m)
	{
		numRows = m.numRows;
		numCols = m.numCols;
		matrix = m.matrix;
		row = m.row.clone();
		col = m.col.clone();
		solution = m.solution;
	}
	
	public void forwardElimination()
	{
		for(int i=0; i<numCols; i++) {
			for(int j=i+1; j<numRows; j++) {
				double multiplier = findMultiplier(this,i,j);
				for(int k=i; k<numCols; k++)
					matrix[j][k] = matrix[j][k] - (matrix[i][k] * multiplier);
				solution[j] = solution[j] - (solution[i] * multiplier);
			}
		}
		adjustRowCol();
		lastRowHasPivot();
		setFreeCols();
		rank = pivcol.size();
	}
	public void backwardElimination()
	{
		for(int i=numRows-1; i>0; i--) {
			for(int j=i-1; j>=0; j--) {
				while(isZeroRow(row[i])) {
					i--;
					j--;
				}
				double multiplier = findMultiplier(this,i,j);
				for(int k=i; k<numCols; k++)
					matrix[j][k] = matrix[j][k] - (matrix[i][k] * multiplier);
			}
		}
	}
	public void divideByPivot()
	{
		for(int i=0; i<numRows; i++) {
			if(!isZeroRow(row[i]))
				for(int j=0; j<numCols; j++)
					matrix[i][j] = matrix[i][j] / matrix[i][pivcol.get(i)];
		}
		adjustRowCol();
	}
	
	private double findMultiplier(Matrix m, int r1, int r2)
	{
		int c1 = r1;
		while(m.matrix[r1][c1] == 0)
			c1++;
		if(r2-r1 == 1)
			pivcol.add(c1);	// Remember this pivot column
		
		return m.matrix[r2][c1] / m.matrix[r1][c1];
	}
	private boolean isZeroRow(double[] r)
	{
		for(double d : r)
			if(d != 0)
				return false;
		return true;
	}
	private void lastRowHasPivot()
	{
		for(int i=0; i<row[numRows-1].length; i++)
			if(row[numRows-1][i] != 0) {
				pivcol.add(i);
				return;
			}
	}
	private void setFreeCols()
	{
		for(int i=0; i<numCols; i++)
			if(!pivcol.contains(i))
				freecol.add(i);
	}
	
	public double[] backSubstituteSquare()
	{
		// Create array to hold values
		double[] answers = new double[numCols];
		answers[numCols-1] = solution[numCols-1] / matrix[numCols-1][numCols-1]; // Used to be matrix[numRows-1][numCols-1]
		
		for(int i=numCols-2; i>=0; i--) {
			double var = 0;
			for(int j=numCols-1; j>i; j--)
				var = var + (matrix[i][j] * answers[j]);
			answers[i] = (solution[i] - var) / matrix[i][i];
		}		
		return answers;
	}
	
	public double[] getParticular()
	{
		double[] particular = new double[numCols];
		// Set every free column to 0
		for(int f : freecol)
			particular[f] = 0;
		
		int i=0;
		for(int p : pivcol) {
			particular[p] = solution[i];
			i++;
		}
		
		return particular;
	}
	public ArrayList<double[]> getSpecial()
	{
		ArrayList<double[]> ret = new ArrayList<double[]>();
		
		// For every free variable, add new array of size col
		for(int f : freecol)
			ret.add(new double[numCols]);
		
		// Insert 1 or zero into corresponding free row of solution
		for(int i=0; i<freecol.size(); i++) {
			int thisFree = freecol.get(i);
			int l=0;
			for(int j=0; j<numCols; j++) {
				if(j == thisFree) {
					double[] thisCol = col[j];
					for(int k=0; k<ret.get(i).length; k++) {
						if(k == thisFree)
							ret.get(i)[k] = 1;
						else if(freecol.contains(k))
							ret.get(i)[k] = 0;
						else {
							ret.get(i)[k] = thisCol[l] * -1;
							l++;
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	public boolean hasNoSolution()
	{
		for(int i=0; i<numRows; i++)
			if(isZeroRow(row[i]) && solution[i] != 0)
				return true;
		return false;
	}
}