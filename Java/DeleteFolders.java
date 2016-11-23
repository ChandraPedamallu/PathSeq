import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class DeleteFolders {
  
    /**
     *    * Deletes the specified diretory and any files and directories in it
     *       * recursively.
     *          * 
     *             * @param dir The directory to remove.
     *                * @throws IOException If the directory could not be removed.
     *                   */
   	public static void main(String[] argv) throws Exception {
         	String typefile=argv[0];
		String name=argv[1];
		if(typefile.equalsIgnoreCase("DIR")){
			deleteDir(new File(name));
		}
		else{
         		removeFile(new File(name));
		}
	 }
      
       private static void removeFile(File file) throws IOException {
           //
	   //    // make sure the file exists, then delete it
	   //        //
	   //
	               if (!file.exists())
	                     throw new FileNotFoundException(file.getAbsolutePath());
	   
	                         if (!file.delete()) {
	                               Object[] filler = { file.getAbsolutePath() };
	                                     String message = "DeleteFailed";
	                                           throw new IOException(message);
	                                               }
	                                                 }
	   //
	   
	   
	                                                
      public static void deleteDir(File dir)
          throws IOException
	    {
	        if (!dir.isDirectory()) {
		      throw new IOException("Not a directory " + dir);
		          }
			      
			          File[] files = dir.listFiles();
				      for (int i = 0; i < files.length; i++) {
				            File file = files[i];
					          
						        if (file.isDirectory()) {
							        deleteDir(file);
								      }
								            else {
									            boolean deleted = file.delete();
										            if (!deleted) {
											              throw new IOException("Unable to delete file" + file);
												              }
													            }
														        }
															    
															        dir.delete();
																  }

																  }






