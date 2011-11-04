import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Main {
	
	//private static Matrix matrix;
	private static int m;
	private static int n;
	
	public static void main(String[] args) {
		
		Matrix inputMatrix = null;
		
		// Read in words from file
		try
		{
			BufferedReader in = new BufferedReader(new FileReader("input8.txt"));

			String line = in.readLine();
			m = Integer.parseInt(line);
			
			line = in.readLine();
			n = Integer.parseInt(line);
			
			int currCol = 0;
			
			inputMatrix = new Matrix(m, n);
						
			// Get solutions
			line = in.readLine();
			inputMatrix.setSolution(parseLine(line, m));
			// Augment solutions to matrix
//			inputMatrix.addCol(inputMatrix.getSolution());
			
			while((line = in.readLine()) != null)
			{
				if (line.charAt(0) != '#' && line.charAt(0) != ' ')
				{
					double[] row = parseLine(line, n);
					inputMatrix.addRow(row, currCol);
					currCol++;
				}
			}
			
			in.close();
			
			// Set rows and columns
			inputMatrix.adjustRowCol();

		}catch (Exception ex)
		{
			System.err.println("Error on file: " + ex.getMessage());
		}

		System.out.println("\nInput Matrix:");
		inputMatrix.print();
		System.out.println();
		
		Matrix eMatrix = new Matrix(m, n);
		eMatrix.copy(inputMatrix);
		eMatrix.forwardElimination();
		eMatrix.print();
		System.out.println();
		
		int rank = eMatrix.getRank();
		if(rank==m && rank==n)	// 1 solution
		{
			System.out.print("There is a unique solution:\n[");
			double[] a = eMatrix.backSubstituteSquare();
			for(int i=0; i<a.length-1; i++)
				System.out.print(a[i] + ", ");
			System.out.print(a[a.length-1] + "]^T\n");
		}
		else if(rank==m && rank<n)	// Infinite solutions
		{
			System.out.println("Infinite solutions");
			
			// Get RREF matrix
			eMatrix.backwardElimination();
			eMatrix.divideByPivot();

			// Get particular
			double[] particular = eMatrix.getParticular();
			// Get special
			ArrayList<double[]> special = eMatrix.getSpecial();
			
			System.out.print("Particular Solution:\n[");
			for(int i=0; i<particular.length-1; i++)
				System.out.print(particular[i] + ", ");
			System.out.print(particular[particular.length-1] + "]^T\n\n");
			
			System.out.print("Special Solution(s):\n");
			ArrayList<Integer> freeCols = eMatrix.getFreeCols();
			for(int i=0; i<freeCols.size(); i++) {
				System.out.print("x" + freeCols.get(i) + "[");
				for(int j=0; j<special.get(i).length-1; j++)
					System.out.print(special.get(i)[j] + ", ");
				System.out.print(special.get(i)[special.get(i).length-1] + "]^T\n");
			}
		}
		else if(rank<m && rank==n)	// 0 or 1 solution
		{
			if(eMatrix.hasNoSolution())
				System.out.println("No Solution");
			else {
				System.out.print("There is a unique solution:\n[");
				double[] a = eMatrix.backSubstituteSquare();
				for(int i=0; i<a.length-1; i++)
					System.out.print(a[i] + ", ");
				System.out.print(a[a.length-1] + "]^T\n");
			}
		}
		else if(rank<m && rank<n)	// 0 or infinite solutions
		{
			try {
				// If there is a zero row with a non zero solution, there are 0 solutions
				if(eMatrix.hasNoSolution())
					System.out.println("No Solution");
				else {
					// Get RREF matrix
					eMatrix.backwardElimination();
					eMatrix.divideByPivot();
					
					// Get particular
					double[] particular = eMatrix.getParticular();
					// Get special
					ArrayList<double[]> special = eMatrix.getSpecial();
					
					System.out.println("Infinite solutions");
					
					System.out.print("Particular Solution:\n[");
					for(int i=0; i<particular.length-1; i++)
						System.out.print(particular[i] + ", ");
					System.out.print(particular[particular.length-1] + "]^T\n\n");
					
					System.out.print("Special Solution(s):\n");
					ArrayList<Integer> freeCols = eMatrix.getFreeCols();
					for(int i=0; i<freeCols.size(); i++) {
						System.out.print("x" + freeCols.get(i) + "[");
						for(int j=0; j<special.get(i).length-1; j++)
							System.out.print(special.get(i)[j] + ", ");
						System.out.print(special.get(i)[special.get(i).length-1] + "]^T\n");
					}
				}
			} catch (Exception e) {
				System.out.println("No Solution");
			}
		}
	}
	
	private static double[] parseLine(String s, int n)
	{
		double[] lineArray = new double[n];
		
		String delims = "[ ]+";
		String[] strArray = s.split(delims);
		
		for(int i=0; i<strArray.length; i++)
			lineArray[i] = Double.parseDouble(strArray[i]);
		
		return lineArray;
	}
}
