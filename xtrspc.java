import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.util.zip.ZipEntry;
import java.util.Base64;
import java.util.Arrays;

public class xtrspc {
	static String endl = "\012";
	static String digits = "0123456789";
	static List<File> filesWalk = new ArrayList<File>();
	public static void main(String[] params) {
		if(params.length != 2) {
			System.out.println("Usage: xtrspc [OldFile] [NewFile]");
			return;
		}
		try {
			String file1path = params[0].trim();
			String file2path = params[1].trim();
			// Filter User Input
			if(!new File(file1path).isFile()) {
				throw new Exception(file1path+": No such file exists");
			}
			if(!new File(file2path).isFile()) {
				throw new Exception(file2path+": No such file exists");
			}
			if(Pattern.compile(Pattern.quote(" ")).matcher(file1path).find()) {
				throw new Exception(file1path+": No whitespace allowed");
			}
			if(Pattern.compile(Pattern.quote(" ")).matcher(file2path).find()) {
				throw new Exception(file2path+": No whitespace allowed");
			}
			// -----------------
			String file1name = file1path.substring(file1path.lastIndexOf("/")+1,file1path.length());
			String file2name = file2path.substring(file2path.lastIndexOf("/")+1,file2path.length());
			String randomNum = randomID();
			String file1Tmp = "file1-"+randomNum;
			String file1dir = file1Tmp.substring(file1Tmp.lastIndexOf("/")+1,file1Tmp.length());
			String file2Tmp = "file2-"+randomNum;
			String file2dir = file2Tmp.substring(file2Tmp.lastIndexOf("/")+1,file2Tmp.length());
			// Decompile APK 1
			String[] commands1 = ("apktool -r d -o "+file1Tmp+" "+file1path).split(" ");
			ProcessBuilder pb1 = new ProcessBuilder().
				command(commands1).
				redirectErrorStream(true);
			Process p1 = pb1.start();
			p1.waitFor();
			BufferedReader br1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
			String content1="";String inputLine1;
			while((inputLine1 = br1.readLine()) != null) {
				content1 += inputLine1 + endl;
			}
			System.out.println(content1);
			br1.close();
			File[] file1ls = fWalk(file1Tmp);
			// Decompile APK 2
			String[] commands2 = ("apktool -r d -o "+file2Tmp+" "+file2path).split(" ");
			ProcessBuilder pb2 = new ProcessBuilder().
				command(commands2).
				redirectErrorStream(true);
			Process p2 = pb2.start();
			p2.waitFor();
			BufferedReader br2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
			String content2="";String inputLine2;
			while((inputLine2 = br2.readLine()) != null) {
				content2 += inputLine2 + endl;
			}
			System.out.println(content2);
			br2.close();
			File[] file2ls = fWalk(file2Tmp);
			// Sets of Data
			// Type: added, removed, modified & unchanged
			// [0]=Name,[1]=Type
			List<String[]> allData = new ArrayList<String[]>();
			// Compare File -- (1) Base on Old File
			for(File f : file1ls) {
				if(f.isDirectory()) {
					continue;
				}
				File fx = new File(f.toString().replace(file1dir,file2dir));
				String fn = f.toString().split(Pattern.quote(file1dir))[1];
				while(fn.startsWith("/")) {
					fn = fn.substring(1,fn.length());
				}
				if(fx.exists()) {
					// File 1 Content
					InputStream stream = new FileInputStream(f);
					int size = stream.available();
					byte[] buffer = new byte[size];
					stream.read(buffer);
					stream.close();
					// File 2 Content
					InputStream is = new FileInputStream(fx);
					int si = is.available();
					byte[] bu = new byte[si];
					is.read(bu);
					is.close();
					if(Arrays.equals(buffer, bu)) {
						System.out.println("[*] UNCHANGED: "+fn);
						allData.add(new String[]{fn,"unchanged",f.toString()});
					} else {
						System.out.println("[!] MODIFIED: "+fn);
						allData.add(new String[]{fn,"modified",f.toString()});
					}
				} else {
					System.out.println("[-] REMOVED: "+fn);
					allData.add(new String[]{fn,"removed",f.toString()});
				}
			}
			// Compare File -- (2) Base on New File
			for(File f : file2ls) {
				if(f.isDirectory()) {
					continue;
				}
				File fx = new File(f.toString().replace(file2dir,file1dir));
				String fn = f.toString().split(Pattern.quote(file2dir))[1];
				while(fn.startsWith("/")) {
					fn = fn.substring(1,fn.length());
				}
				if(!fx.exists()) {
					System.out.println("[+] ADDED: "+fn);
					allData.add(new String[]{fn,"added",f.toString()});
				}
			}
			// Create Log
			StringBuilder added = new StringBuilder();
			added.append("+++++ Added files:"+endl);
			StringBuilder removed = new StringBuilder();
			removed.append("+++++ Removed files:"+endl);
			StringBuilder modified = new StringBuilder();
			modified.append("+++++ Modified files:"+endl);
			StringBuilder unchanged = new StringBuilder();
			unchanged.append("+++++ Unchanged files:"+endl);
			for(String[] sx : allData) {
				switch(sx[1]) {
					case "added":
						added.append(sx[0]+endl);
						break;
					case "removed":
						removed.append(sx[0]+endl);
						break;
					case "modified":
						modified.append(sx[0]+endl);
						break;
					case "unchanged":
						unchanged.append(sx[0]+endl);
						break;
				}
			}
			added.append(endl);
			removed.append(endl);
			modified.append(endl);
			unchanged.append(endl);
			// Generate Compare Result
			System.out.println("Generate compare result: xtrspc-"+file1name+"_"+file2name+".txt");
			FileOutputStream out = new FileOutputStream("xtrspc-"+file1name+"_"+file2name+".txt");
			out.write((added.toString()+removed.toString()+modified.toString()+unchanged.toString()).getBytes());
			out.close();
			// Extract specific file based on the compare result(added, modified)
			System.out.println("Remove unchanged files");
			for(File f : file1ls) {
				if(f.isDirectory()) {
					continue;
				}
				boolean isfetch = false;
				for(String[] sx : allData) {
					if(sx[2].equals(f.toString()) && (sx[1].equals("added")||sx[1].equals("modified"))) {
						isfetch = true;
						break;
					}
				}
				if(!isfetch) {
					f.delete();
				}
			}
			for(File f : file2ls) {
				if(f.isDirectory()) {
					continue;
				}
				boolean isfetch = false;
				for(String[] sx : allData) {
					if(f.toString().endsWith(sx[0]) && (sx[1].equals("added")||sx[1].equals("modified"))) {
						isfetch = true;
						break;
					}
				}
				if(!isfetch) {
					f.delete();
				}
			}
			file1ls = fWalk(file1Tmp);
			file2ls = fWalk(file2Tmp);
			System.out.println("Fetch and Compress important data from compare result");
			ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream("xtrspc-"+file1name+"-old.zip")));
			BufferedInputStream origin = null;
			int blocksize = 1024;
			byte[] data = new byte[blocksize];
			for(File f : file1ls) {
				if(f.isDirectory()) {
					continue;
				}
				System.out.println("inflating: "+f.toString());
				String fn = f.toString().split(Pattern.quote(file1dir))[1];
				while(fn.startsWith("/")) {
					fn = fn.substring(1,fn.length());
				}
				FileInputStream fis = new FileInputStream(f);
				origin = new BufferedInputStream(fis, blocksize);
				try {
					ZipEntry entry = new ZipEntry(fn);
					zipOut.putNextEntry(entry);
					int count;
					while((count = origin.read(data, 0, blocksize)) != -1) {
						zipOut.write(data, 0, count);
					}
					zipOut.closeEntry();
				} finally {
					origin.close();
				}
			}
			zipOut.finish();
			zipOut.close();
			zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream("xtrspc-"+file2name+"-new.zip")));
			origin = null;
			data = new byte[blocksize];
			for(File f : file2ls) {
				if(f.isDirectory()) {
					continue;
				}
				System.out.println("inflating: "+f.toString());
				String fn = f.toString().split(Pattern.quote(file2dir))[1];
				while(fn.startsWith("/")) {
					fn = fn.substring(1,fn.length());
				}
				FileInputStream fis = new FileInputStream(f);
				origin = new BufferedInputStream(fis, blocksize);
				try {
					ZipEntry entry = new ZipEntry(fn);
					zipOut.putNextEntry(entry);
					int count;
					while((count = origin.read(data, 0, blocksize)) != -1) {
						zipOut.write(data, 0, count);
					}
					zipOut.closeEntry();
				} finally {
					origin.close();
				}
			}
			zipOut.finish();
			zipOut.close();
			System.out.println("Clean "+file1Tmp);
			deleteRecursively(new File(file1Tmp));
			System.out.println("Clean "+file2Tmp);
			deleteRecursively(new File(file2Tmp));
			System.out.println("Done.");
		} catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	public static String randomID() {
		String randId = "";
		while(randId.length() < 5) {
			int index = (int)Math.floor(Math.random()*digits.length());
			randId += digits.substring(index, index+1);
		}
		return randId;
	}
	public static void deleteRecursively(File file) {
		try {
			if(file.isDirectory()) {
				for(File f : file.listFiles()) {
					deleteRecursively(f);
				}
			}
			file.delete();
		} catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	public static void FileWalk(File file) {
		try {
			File[] fls = file.listFiles();
			for(File fi : fls) {
				if(fi.isDirectory()) {
					FileWalk(fi);
				} else {
					filesWalk.add(fi);
				}
			}
		} catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	public static File[] fWalk(String f) {
		try {
			filesWalk.clear();
			FileWalk(new File(f));
			return filesWalk.toArray(new File[0]);
		} catch(Exception e) {
			System.out.println(e.toString());
			return new File[]{};
		}
	}
}
