package sse.mm.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import sse.mm.Make;

public class MakeWriter {

	private final String makeFile;
	private final Make make;

	public MakeWriter(String makeFile, Make make) {
		this.makeFile = makeFile;
		this.make = make;
	}

	public void write() {
		if (askWrite().equals("y")) {
			File cf = new File(makeFile);
			File nf = new File(makeFile + ".ba");
			if (!cf.renameTo(nf)) {
				System.err.println("could not back up Makefile at '" + makeFile
						+ "'.");
				System.err.println("abort.");
				return;
			}

			doWrite(cf);
		} else {
			System.out.println("generated Makefile is like below:\n");
			System.out.println(make.format());
		}
	}

	private void doWrite(File cf) {

		BufferedWriter br = null;
		try {
			br = new BufferedWriter(new FileWriter(cf));
			br.write(make.format());
			br.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private String askWrite() {
		System.out.println("Would you like to modify the MakeFile at '"
				+ makeFile + "' ? [y/n]");
		Scanner scanner = new Scanner(System.in);
		String s = scanner.nextLine();
		scanner.close();
		return s;
	}
}
