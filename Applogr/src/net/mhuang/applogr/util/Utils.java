package net.mhuang.applogr.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import net.mhuang.applogr.R;

import android.content.Context;
import android.os.Build;
import android.util.Log;

public class Utils {

    static byte[] buffer = new byte[1024]; //Reserve 512B for file reads
	static FileInputStream is;
	
	public static boolean ifFileExists(String path) {
		return new File(path).exists();
	}
	
	//Custom endchar
	public static String readFile(String file, char endChar) {
        try {
        	
            is = new FileInputStream(file);
            int len = is.read(buffer);
            is.close();

            if (len > 0) {
                int i;
                for (i=0; i<len; i++) {
                    if (buffer[i] == endChar) {
                        break;
                    }
                }
                return new String(buffer, 0, 0, i);
            }
        } catch (java.io.FileNotFoundException e) {
        	//Log.d("setcpu","ioexception "+e);
        } catch (java.io.IOException e) {
        	//Log.d("setcpu","ioexception "+e);
        }
        return null;
    }
	
	//Default endchar of \0
	public static String readFile(String file) {
        try {
            is = new FileInputStream(file);
            int len = is.read(buffer);
            is.close();

            if (len > 0) {
                int i;
                for (i=0; i<len; i++) {
                    if (buffer[i] == '\0') {
                        break;
                    }
                }
                return new String(buffer, 0, 0, i);
            }
        } catch (java.io.FileNotFoundException e) {}
        catch (java.io.IOException e) {}
        return null;
    }
	
	//Custom buffer
	public static String readFile(String file, char endChar, byte[] buffer) {
        try {
        	
            is = new FileInputStream(file);
            int len = is.read(buffer);
            is.close();

            if (len > 0) {
                int i;
                for (i=0; i<len; i++) {
                    if (buffer[i] == endChar) {
                        break;
                    }
                }
                return new String(buffer, 0, 0, i);
            }
        } catch (java.io.FileNotFoundException e) {
        	//Log.d("setcpu","ioexception "+e);
        } catch (java.io.IOException e) {
        	//Log.d("setcpu","ioexception "+e);
        }
        return null;
    }

	public static void copyFile(File src, File dst) throws IOException {
	      FileChannel inChannel = new FileInputStream(src).getChannel();
	      if(!dst.exists()) {
	    	  //dst.createNewFile();
	      }
	      FileChannel outChannel = new FileOutputStream(dst).getChannel();
	      try {
	         inChannel.transferTo(0, inChannel.size(), outChannel);
	      } finally {
	         if (inChannel != null) {
	            inChannel.close();
	         }
	         if (outChannel != null) {
	            outChannel.close();
	         }
	      }
	   }
	
    public static Integer[] convertStringArray(String[] sarray) {
		 if (sarray != null) {
			 Integer intarray[] = new Integer[sarray.length];
		 for (int i = 0; i < sarray.length; i++) {
			 //Log.d("setcpu", sarray[i]);
			 intarray[i] = Integer.parseInt(sarray[i].trim());
		 }
		 	return intarray;
		 }
		 	return null;
		 }
	
   public static String[] convertIntArray(int[] sarray) {
		 if (sarray != null) {
		 String intarray[] = new String[sarray.length];
		 for (int i = 0; i < sarray.length; i++) {
		 intarray[i] = ""+sarray[i];
		 }
		 return intarray;
		 }
		 return null;
		 }

public static Integer[] convertStringArrayToInteger(String[] sarray) {
	 if (sarray != null) {
		 Integer[] intarray = new Integer[sarray.length];
		 for (int i = 0; i < sarray.length; i++) {
			 intarray[i] = Integer.parseInt(sarray[i].trim());
		 }
		 return intarray;
	 	}
	 	return null;
	 }

public static String[] convertIntArray(Integer[] sarray) {
	 if (sarray != null) {
		 String intarray[] = new String[sarray.length];
		 for (int i = 0; i < sarray.length; i++) {
			 intarray[i] = ""+sarray[i];
		 }
		 	return intarray;
	 	}
	 	return null;
	 }

public static int getIndex(String f, String[] array) {
	if(f == null || array == null)
		return 0;
	
	for (int i = 0; i < array.length; i++) {
		if (array[i].equals(f) || f.equals(array[i])) {
			return i;
		}
	}
	return 0;
}

/* Get the yeshup binary, used for starting child processes so that they will
 * exit even if this process gets a SIGKILL. See yeshup/jni/yeshup.c and
 * yeshup/jni/yeshup.S.
 * 
 * Returns: path to the yeshup binary, extracting it if it doesn't exist; sh -c
 * on failure.
 */
public static String getYeshup(Context context) {
	File dir = context.getDir("bin",
			Context.MODE_PRIVATE);
	File yeshup = new File(dir.getAbsolutePath() + "/yeshup");
		
	/* Check if yeshup exists */
	if(yeshup.exists()) {
		try {
			/* Return its path if it's executable */
			if(Runtime.getRuntime().exec(yeshup
					.getAbsolutePath()).waitFor() != 255) {
				throw new IOException();
			}
			return yeshup.getAbsolutePath();
		}
		/* yeshup exists but could not execute: assume permissions are bad and
		 * try fixing permissions. If fixing permissions fails, we assume all
		 * hope is lost and return "" so we don't fail.
		 */
		catch(IOException e) {
			try {
				if(Runtime.getRuntime().exec("chmod 744 "+yeshup
						.getAbsolutePath()).waitFor() != 0) {
					throw new IOException();
				}
				
				/* Can we execute now? */
				if(Runtime.getRuntime().exec(yeshup
						.getAbsolutePath()).waitFor() != 255) {
					throw new IOException();
				}
				return yeshup.getAbsolutePath();
			} catch (InterruptedException e1) {}
			  catch (IOException e1) {
				  /* Can't fix permissions or can't execute. Probably an
				   * unsupported platform that we previously assumed was
				   * armeabi. Just use the unsafe nothing.
				   */
				  e1.printStackTrace();
				  return "";  
			  }
		}
		catch (InterruptedException e) {}
	}
	
	/* yeshup doesn't exist. Find ABI we need */
	InputStream ins = null;
	
	/* CPU_ABI2 property is only on FROYO and above */
	String CPU_ABI2 = null;
	
	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
		try {
			CPU_ABI2 = Build.CPU_ABI2;
		} catch(NoSuchFieldError e) {}
	}
	
	/* Catch most armeabi-v7a */
	if("armeabi-v7a".equals(Build.CPU_ABI) ||
			"armeabi-v7a".equals(CPU_ABI2)) {
		ins = context.getResources().openRawResource(
				R.raw.yeshup_armeabiv7a);
	}
	/* Catch most armeabi. If the above check fails on an armeabi-v7a platform,
	 * this should catch it anyway and extract a still-compatible binary.
	 */
	else if(Build.CPU_ABI.contains("armeabi") ||
			CPU_ABI2.contains("armeabi")) {
		ins = context.getResources().openRawResource(
				R.raw.yeshup_armeabi);
	}
	/* Catch (most?) x86 */
	else if("x86".equals(Build.CPU_ABI) ||
			"x86".equals(CPU_ABI2)) {
		ins = context.getResources().openRawResource(
				R.raw.yeshup_x86);
		//Log.d("setcpu", "x86");
	}
	/* Catch (most?) MIPS */
	else if("mips".equals(Build.CPU_ABI) ||
			"mips".equals(CPU_ABI2)) {
		ins = context.getResources().openRawResource(
				R.raw.yeshup_mips);
	}
	/* CPU ABI unknown: try ARM before failing */
	else {
		ins = context.getResources().openRawResource(
				R.raw.yeshup_armeabi);
	}
		
	/* Extract the yeshup binary found above. */
	try {
		int size = ins.available();

		byte[] buffer = new byte[size];

		ins.read(buffer);
		ins.close();

		FileOutputStream fos = new FileOutputStream
				(yeshup.getAbsolutePath());
	
		fos.write(buffer);
	
		fos.close();
		
		/* Make executable - return value of chmod should be 0 */
		if(Runtime.getRuntime().exec("chmod 744 "+yeshup
				.getAbsolutePath()).waitFor() != 0) {
			throw new IOException();
		}
		
		/* Return value of yeshup with no arguments should ALWAYS be 255 */
		if(Runtime.getRuntime().exec(yeshup
				.getAbsolutePath()).waitFor() != 255) {
			throw new IOException();
		}
	
		return yeshup.getAbsolutePath();
		/* Something went wrong: our architecture isn't supported or we
		 * couldn't make the file executable for some reason.
		 * 
		 * In this case, use nothing, which is more dangerous, but at least it
		 * works. */
	} catch (IOException e) {
		e.printStackTrace();
		return "";
	} catch (InterruptedException e) {
		e.printStackTrace();
		return "";
	}
}
}

