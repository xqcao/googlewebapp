package com.webgoogle.demo;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DeepLearn {
	public  final int MAX_SIZE = 50;
	public  int words;
	public  HashMap<String, float[]> findwordvec = new HashMap<String, float[]>();

	protected  void setWords(int i) {
		words = i;
	}

	public  String savenulltxtpath = null;
	public String messageAll = null;
	public  String doDeepLearn(String googlefile, String giveWord,String topIndex){
		boolean flag = checkisword(giveWord);
		String resultString = null;
		String message = null;
		if (flag == true) {
			loadModel(googlefile);
			int topNo = new Integer(topIndex);
			Object re_wdvec[] = fromword2distance(giveWord, topNo);
			String[] array1 = (String[]) re_wdvec[0];
			float[] array2 = (float[]) re_wdvec[1];
			// savetopwords(array1, array2, saveFileName);
			resultString =  savetopwords(array1, array2);
		} else {
			message = "this is a similarity search(nearest-neighbour search, "
					+ "we just search if a word contains only alphabets, "
					+ "please remove all Non-alphabetic components, top_number shouldn't be empty,"
					+ "then try again! "
					+ "more detail please check: https://code.google.com/p/word2vec/";
			// printemptytxt(saveFileName,message);
			System.out.println(message);
		}
		messageAll = message;
		return resultString + messageAll;
	}

	public static boolean checkisword(String str) {// check is a word or not
		if (str.matches("\\w+") == true) {
			return true;
		} else {
			return false;
		}
	}

	public  String savetopwords(String[] Object1, float[] Object2) {
		StringBuilder sb = new StringBuilder();
		for (int ix = 0; ix < Object2.length; ix++) {
			//System.out.println(Object1[ix] + " ------ " + Object2[ix] + "\n");
			sb.append(Object1[ix]).append("   ------   ").append(Object2[ix]).append("vxvxvx");
		}
		return sb.toString();
	}

	/*
	 * public static void savetopwords(String[] Object1, float[] Object2,
	 * String filename) { try { BufferedWriter out = new BufferedWriter(new
	 * FileWriter(filename)); for (int ix = 0; ix < Object2.length; ix++) {
	 * out.write(Object1[ix] + " ------ " + Object2[ix]); out.newLine(); }
	 * out.close(); } catch (Exception e) { e.printStackTrace(); } }
	 */
	public  Object[] fromword2distance(String giveword, int top) {
		float[] vecOfWord = findwordvec.get(giveword);
		if (vecOfWord == null) {
			String message = "the word not exists in my dictionary, in this searching process, "
					+ "we use small version of the word database "
					+ "your search is out of this database,"
					+ "please set another one and try again! "
					+ "more detail please check: https://code.google.com/p/word2vec/";
			System.out.println(message);
			// printemptytxt(savenulltxtpath,message);
			System.err.println("a Null vector!"); //
			messageAll = message;
		}
		float[] awk = new float[words];
		String[] wordwv = new String[words];
		Map<String, Float> source = new HashMap<String, Float>();
		int r = 0;
		for (Entry<String, float[]> w : findwordvec.entrySet()) {
			float wv = 0;
			for (int ii = 0; ii < vecOfWord.length; ii++) {
				wv += (w.getValue()[ii]) * (vecOfWord[ii]);
			}
			source.put(w.getKey(), wv);
			awk[r] = wv;
			wordwv[r] = w.getKey();
			r++;
		}
		float[] bb = new float[top];
		String[] wwbb = new String[top];
		for (int ii = 0; ii < awk.length; ii++) {
			float contawk = awk[ii];
			String conttopword = wordwv[ii];
			for (int j = 0; j < bb.length; j++) {
				float temp;
				String tempw;
				if (contawk > bb[j]) {
					temp = contawk;
					contawk = bb[j];
					bb[j] = temp;
					tempw = conttopword;
					conttopword = wwbb[j];
					wwbb[j] = tempw;
				}
			}
		}
		return new Object[] { wwbb, bb };
	}

	public  HashMap<String, float[]> getFindwordvec() {
		return findwordvec;
	}

	public void setFindwordvec(HashMap<String, float[]> findwordvec) {
		DeepLearn dL = new DeepLearn();
		dL.findwordvec = findwordvec;
	}

	public float[] getWordVector(String word) {
		return findwordvec.get(word);
	}

	public void loadModel(String path) {
		DataInputStream dis = null;
		BufferedInputStream bis = null;
		double len = 0;
		float vector = 0;
		try {
			bis = new BufferedInputStream(new FileInputStream(path));
			dis = new DataInputStream(bis);
			dis = new DataInputStream(bis);
			int words = new Integer(readString(dis));
			int size = new Integer(readString(dis));
			String word;
			float[] vectors = null;
			// words = 50000;
			setWords(words);//
			// setWords(50000);
			for (int i = 0; i < words; i++) {// words=100000
				word = readString(dis);
				vectors = new float[size];
				len = 0;
				for (int j = 0; j < size; j++) {
					vector = readFloat(dis);
					len += vector * vector;
					vectors[j] = (float) vector;
				}
				len = Math.sqrt(len);
				for (int j = 0; j < vectors.length; j++) {
					vectors[j] = (float) (vectors[j] / len);
				}
				findwordvec.put(word, vectors);
				dis.read();
			}
		} catch (FileNotFoundException e) {
			String message = "word2vec file can not found, about this file,"
					+ "please check: https://code.google.com/p/word2vec/";
			System.out.println(message);
			// printemptytxt(savenulltxtpath,message);
			messageAll = message;
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
					bis = null;
				}
				if (dis != null) {
					dis.close();
					dis = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public float readFloat(InputStream is) throws IOException {
		byte[] bytes = new byte[4];
		is.read(bytes);
		return getFloat(bytes);
	}

	public float getFloat(byte[] b) {
		int accum = 0;
		accum = accum | (b[0] & 0xff) << 0;
		accum = accum | (b[1] & 0xff) << 8;
		accum = accum | (b[2] & 0xff) << 16;
		accum = accum | (b[3] & 0xff) << 24;
		return Float.intBitsToFloat(accum);
	}

	public String readString(DataInputStream dis) throws IOException {
		byte[] bytes = new byte[MAX_SIZE];
		byte b = dis.readByte();
		int i = -1;
		StringBuilder sb = new StringBuilder();
		while (b != 32 && b != 10) {
			i++;
			bytes[i] = b;
			b = dis.readByte();
			if (i == 49) {
				sb.append(new String(bytes));
				i = -1;
				bytes = new byte[MAX_SIZE];
			}
		}
		sb.append(new String(bytes, 0, i + 1));
		return sb.toString();
	}
}