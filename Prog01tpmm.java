/* 
 *  @file  : Prog01tpmm.java
 *  @brief : To create a sorted list in HD(used for search - made easy) , when the input is an unsorted list.
 *  @ remarks
 *             Course       :  Advanced Database Systems CS 6543
 *             Due Date     :  2/28/2018
 *             Instructor   :  Dr.Hung-Chi Su
 *
 *             
 *  @author       : Mahathi Sudhana
 *  @date         : 2/25/2018
 *  @Description  : Below is the program for sorting the data using 2 phases. Phase 1 - create sublist based on memory and block size and sort them. 
					Phase - 2 , use the sorted sublist to create a overall final sorted data in HD
	
	NOTE : THE INPUT FILE USED IN THE PROGRAM IS AT THE END OF TEH FILE COMMENTED. AM NOT ATTACHING A SEPEARTE INPUT FILE FOR THIS. 
	IF YOU WANT TO TEST WITH DIFFERENT DATA YOU HAVE TO CHNAGE THE INPUT FILE MANUALLY.
	
	IF AM CHNAGING IT TO PDF FILE MY INTENDATION IS GOING WRONG IF I COPY THE CODE AGAIN INTO A FILE , WHERE I CAN EXECUTE SO AM LEAVING THE .JAVA FILE AS IT IS.
*/ 
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SearchInMem {
	public static void main(String[] args)
	{
		Integer arrHD[] = new Integer[8192];
		final Integer mem[] = new Integer[30];	// final memory size
		final int blockSize = 5;				// final block size
		int numberOfBlocks = (mem.length/blockSize)-1;	//number of blocks used in Phase II
		int blockPointer[]= new int[numberOfBlocks];	// block pointer used for storing the index values of blocks in Phase II
		int initialBP[] = new int[numberOfBlocks];		// used to get back to initial block pointers 
		double n=0.0;
		
		//File file = new File("input.txt");
		Scanner s;
		try {	
			s = new Scanner(new File("input.txt"));
			if (s.hasNext())
			{
				 n = s.nextInt();
				 arrHD[0]=(int)n;
				System.out.println("The total number of elements are :"+arrHD[0]);
			}
			for(int i =1; i<=n; i++)
			{
				arrHD[i]=s.nextInt();
				System.out.print(" "+arrHD[i]);
			}
			//s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("\n");
		double p1 = n/mem.length;  // number of sublists
		int numberOfSublist=(int) Math.ceil(p1);
		double x =numberOfSublist;  // for calculating the total number of sublist at last
		double totallist =x,sum=0;
		int cout =0;
		while(sum  >= 0)		// used to calculate the total sublist
		{
			
			sum = Math.ceil(x/numberOfBlocks);
			totallist = Math.ceil(totallist+sum);
			x=sum;
			cout++;
			if(sum == 1)
			{
				sum = -1;
			}
		}
		x =numberOfSublist;totallist =x;sum=0;cout =cout+2;
		int numberlist[] = new int[cout];
		int arrindx=1;
		numberlist[arrindx]=numberOfSublist;
		arrindx++;
		while(sum  >= 0)		// same while loop but used to keep the track of total list and copy in an array used in program later on
		{
			sum = Math.ceil(x/numberOfBlocks);
			totallist = Math.ceil(totallist+sum);
			numberlist[arrindx]=(int) totallist;
			x=sum;
			cout++;
			if(sum == 1)
			{
				sum = -1;
			}
			arrindx++;
		}
		int totalSublist = (int) totallist;
		int nextsublist = numberOfSublist/numberOfBlocks;		
		int lastsublistSize = (int)n%mem.length;
		int hdsublist[] = new int[totalSublist+1];		// array used for storing the starting index values in an array
		int hdsublistend[] = new int[totalSublist+1];	// array used for storing the end index values in an array
		int count =1,j=1;
		int pos =(int)n+2;
		int memsize=mem.length;
		int newSublist =0;
		SearchInMem sc = new SearchInMem();
		System.out.println("\nSorted elements in Phase I");
		while(count<=numberOfSublist)		// loop used to store values from input file to hard disk
		{
			for(int i=0;i<memsize;i++)
			{
				if(j<=n)
				{
					mem[i]=arrHD[j];
					j++;
				}
				if(j>n)
				{
					memsize=i+1;
					lastsublistSize=memsize;
				}
			}
			sc.quicksort(mem, 0, memsize-1);
			for(int i=0;i<memsize;i++)
			{
				System.out.print(" "+mem[i]);
			}
			System.out.println("\n");
			hdsublist[count]=pos;
			hdsublistend[count]=pos+memsize;
			for(int i=0;i<memsize;i++)
			   {
					arrHD[pos]=mem[i];
					pos++;
			   }
			count++;
		}
		int newpos = pos;		// newpos and newposend are used to store the index values for newly generated list in phase II
		int newposend =0;
		for(int i=0;i<mem.length;i++)
		{
			mem[i]= -1;
		}
		pos=pos+1;		// incrementing for maintaining some gap 
		memsize=mem.length;
		int finalPrint1 = pos+(numberOfSublist*memsize);
		int outputBlock = memsize-blockSize;
		int originaloutputBlock = outputBlock;
		int copylist=1,subarray=0,temp=0,tempblocksize=blockSize,repeatlist=0;
		int min=0,indexBP=0,indexMem=0;
		blockPointer[0] = 0;
		initialBP[0]=0;
		for(int i=1;i<numberOfBlocks;i++)		// initializing the pointers of each block to its original value
		{
			blockPointer[i]= blockPointer[i-1]+blockSize;
			initialBP[i]=blockPointer[i];
		}
			//initial load 
		while(copylist<=numberOfSublist )		// initial load from hard disk to memory
		{
		    subarray=hdsublist[copylist];
		    while(temp<tempblocksize && tempblocksize<=outputBlock)
		    {
		    	if(arrHD[subarray]!= null)
		    			mem[temp]=arrHD[subarray];
		    	temp++;
		    	subarray++;
		    	hdsublist[copylist]=subarray;
		    	repeatlist=copylist;
		    }
		    copylist++;
		    tempblocksize=blockSize+tempblocksize;
		}
		copylist=1;
		int memory=numberOfBlocks,dummymemory=0;
		System.out.println("Sorted array in Phase II is \n");
		while(copylist  < totalSublist)// this loop helps to find the smallest and put it in last block of memory and then once the last block is full copies the data to hard disk
		{
			if(numberOfSublist+1 < totalSublist)
				hdsublist[numberOfSublist+1]=pos;
			for(int i=1;i<arrindx;i++)
			{
				if(numberOfSublist+1 == numberlist[i]+1 ||numberOfSublist+1 == numberlist[i] )
				{
					newposend = pos +(memory*mem.length);
					dummymemory = memory;
					memory=memory*2;
				}
				else
				{
					newposend = pos +(dummymemory*mem.length);
				}
					  
			}
			while(mem[outputBlock]==-1 && outputBlock < memsize && pos<=newposend )	// if any block is empty it loads the new data from the list and updates the pointer for block
			{
			    for(int i=0;i<numberOfBlocks;i++)
			    {
			    		
			    	if(blockPointer[i] == initialBP[i]+blockSize)
			    	{
			    		blockPointer[i]=initialBP[i];
			    		subarray=hdsublist[copylist+i];
			    		if(subarray<hdsublistend[copylist+i])
			    		{
			    			for(int k=blockPointer[i];k<(blockPointer[i]+blockSize);k++)
				    		{
				    			if(subarray<hdsublistend[copylist+i])
				    			{
			    					mem[k]=arrHD[subarray];
					    			subarray++;
				    			}
			    			}
				    		hdsublist[copylist+i]=subarray;
			    		}
			    	}
			    }
			    int minbp=0,a=1;
			    min=mem[blockPointer[minbp]];
			    indexMem = blockPointer[minbp];	
				indexBP = minbp;
			    while(a<blockPointer.length)		// find the minimum 
				{
					if(min<mem[blockPointer[a]] && (min!= -1 && mem[blockPointer[a]]!=-1))
					{  
					}
					else
					{
						if(mem[blockPointer[a]]!=-1)
						{
							min=mem[blockPointer[a]];
							indexMem = blockPointer[a];
							indexBP =a; 
						}
								   
					}
					a++; 
				}
			    mem[outputBlock]=min;
			    mem[indexMem]= -1;
			    blockPointer[indexBP]=blockPointer[indexBP]+1;
			    outputBlock++;
			    if(outputBlock == memsize )		// copies to hard disk
			    {
			    	for(int k=originaloutputBlock;k<memsize;k++)
			    	{
			    		arrHD[pos]=mem[k];
			    		if(arrHD[pos]!= -1)
			    			System.out.println(arrHD[pos]+" "+pos); // printing the final hard disk values after sorting
				    	mem[k]=-1;
				    	pos++;
			    	}
			    	outputBlock = originaloutputBlock;
			    }
			}
			hdsublistend[numberOfSublist+1]=pos-1;
			copylist = copylist+numberOfBlocks;
			numberOfSublist++;
			pos=pos+1;			    
			for(int i =0 ;i<numberOfBlocks;i++)
			{
			    blockPointer[i] = initialBP[i]+blockSize;
			}
			    
		}
	}
	// below are the methods used for sorting in phase I - quick sort is used here
	public void quicksort(Integer[] A, int low, int high)
	{
		int pivot;
		if(high>low)
		{
			pivot=Partition(A,low,high);
			quicksort(A,low,pivot-1);
			quicksort(A,pivot+1,high);
		}
	}
	
	public static int Partition(Integer[] A, int low, int high)
	{
		int left, right, pivot_item=A[low];
		left=low;
		right=high;
		
		while(left<right)
		{
			while( left <= right && A[left] <= pivot_item)
				left++;
			while(A[right]>pivot_item)
				right--;
			
			if(left<right)
				swap(A,left,right);
		}
		A[low]=A[right];
		A[right]=pivot_item;
		return right;
	}
	
	public static void swap(Integer[] A, int left, int right)
	{
		int temp =0;
		temp = A[left];
		A[left]= A[right];
		A[right]= temp;
	}
	

}
/* INPUT FILE  :

70 127 4 117 22 16 31 169 58 24 72 19 45 55 159 151 69 60 66 75 38 37 124 30 80 9 121 11 71 133 57 150 157 162 93 160 153 89 39 46 25 15 140 137 82 114 26 12 144 152 32 88 43 147 96 35 111 100 95 132 53 115 122 163 5 81 54 40 129 158 73

*/