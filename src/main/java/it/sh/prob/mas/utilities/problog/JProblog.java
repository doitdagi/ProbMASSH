package it.sh.prob.mas.utilities.problog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Objects;
import java.util.function.Function;

 
public class JProblog implements Function<String, String> {

	static final char NEW_LINE = '\n';

	private final ProblogProcessor processor;

	public static final String HELP = "Usage: java -jar jproblog.jar <input_file> <output_file>\n\n";

	/**
	 * Constructs a new JProblog.
	 */
	public JProblog() {
		this.processor = new ProblogProcessor();
		this.processor.startInstallation();
	}

	/**
	 * Returns the result of running ProbLog with the given input.
	 * 
	 * @param input
	 *            input
	 * @return the result of running ProbLog with the given input
	 */
	@Override
	public String apply(String input) {
		Objects.requireNonNull(input);
		return this.processor.apply(input);
	}

	
	/**
	 * Implemented by Dagi
	 * @param reader
	 * @return
	 */
	public String apply(BufferedReader reader) {
		Objects.requireNonNull(reader);
		return this.processor.apply(read(reader));
	}
	
	String read(BufferedReader reader) {
		StringBuilder sb = new StringBuilder();
		reader.lines().forEach(line -> {
			sb.append(line);
			sb.append(NEW_LINE);
		});
		return sb.toString();
	}

	void write(BufferedWriter writer, String content) throws IOException {
		writer.write(content);
		writer.flush();
	}

	/**
	 * Reads the input from the reader, executes ProbLog, and writes the output
	 * to the writer.
	 * 
	 * @param reader
	 *            reader
	 * @param writer
	 *            writer
	 */
	public void apply(Reader reader, Writer writer) {
		Objects.requireNonNull(reader);
		Objects.requireNonNull(writer);
		try {
			String input = read(new BufferedReader(reader));
			String output = apply(input);
			write(new BufferedWriter(writer), output);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof JProblog)) {
			return false;
		} else {
			JProblog other = (JProblog) obj;
			return this.getProcessor().equals(other.getProcessor());
		}
	}

	@Override
	public int hashCode() {
		return this.processor.hashCode();
	}

	@Override
	public String toString() {
		return this.processor.toString();
	}

	/**
	 * Returns the processor used to execute ProbLog.
	 * 
	 * @return the processor used to execute ProbLog
	 */
	public ProblogProcessor getProcessor() {
		return this.processor;
	}

	/**
	 * This is the main method that is executed from the command line.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		JProblog instance = new JProblog();
		if (args.length == 2) {
			try {
				instance.apply(new FileReader(args[0]), new FileWriter(args[1]));
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		} else {
			System.out.println(HELP);
		}
	}

}