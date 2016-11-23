/*
 * Author: Chandra Sekhar Pedamallu
 * Usage: contains methods for FileSplitter.java
 * Details: Splits a file into multiple files.
 * Adapted From: http://www.go4expert.com/forums/showthread.php?t=236
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class SplitFiles

{
	FileInputStream fstream;
	DataInputStream in;
	BufferedReader br;
	String strLine;

	int len;
	int splitlen;
	String str;
	String chosenFileName;
	int makeNewDir= 0;

	long numberOfLines= 0;
	int numberOfFiles;

	SplitFiles(String fileName,int splitlength, String outFileName, int makedir)
	{
		try
		{
			fstream= new FileInputStream(fileName);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			str=fileName;
			len=0;
			splitlen=splitlength;
			chosenFileName= outFileName;
			makeNewDir= makedir;
			Split();

		}
		catch(FileNotFoundException e)
		{
			System.out.println("File not found.");
		}

	}
	void Split()
	{

		try
		{
			numberOfFiles=0;
			String overflow= "arbitrary";
			boolean overflowLine= false;
			int thisSplitLen= splitlen;
			if ( makeNewDir == 3 )
				(new File(str+"_spt")).mkdir();
			while( (strLine = br.readLine()) != null )
			{
				String fileName;
				if ( makeNewDir == 1 ) {
					(new File(str+"_"+(numberOfFiles+1)+"_spt")).mkdir();
					fileName= str+"_"+(numberOfFiles+1)+"_spt/"+chosenFileName;
				} else if ( makeNewDir == 2 ) {
					fileName= str+"_"+(numberOfFiles+1)+"_"+chosenFileName;
				} else if ( makeNewDir == 3 ) {
					fileName= str+"_spt/"+str+"_"+(numberOfFiles+1)+"_"+chosenFileName;
				} else
					throw new IllegalArgumentException("Must specify whether to create new directories or not.");

				PrintWriter outstream = new PrintWriter( fileName );
				BufferedWriter out = new BufferedWriter(outstream);

				if ( !overflow.equals("arbitrary") ) {
					out.write(overflow + "\n");
					overflowLine= true;
				}
				if (overflowLine)
					thisSplitLen = splitlen-2;
				else
					thisSplitLen = splitlen-1;
				numberOfLines++;
				out.write(strLine + "\n");
				while ( (strLine = br.readLine()) != null && len<thisSplitLen )
				{
					numberOfLines++;
					out.write(strLine + "\n");
					len++;
				}
				if ( len == thisSplitLen ) {
					numberOfLines++;
					overflow = strLine;
				}
				len=0;
				out.flush();
				out.close();
				numberOfFiles++;
			}
			in.close();
		}
		
		catch(Exception e)
		{
			System.out.println("gg");
			e.printStackTrace();
		}
	}

	public long getNumLines() {
		return numberOfLines;
	}

	public int getNumFiles() {
		return numberOfFiles;
	}



}
