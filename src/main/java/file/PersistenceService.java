package file;

import java.io.File;
import java.io.IOException;

import utils.Version;
import diagram.Diagram;

/**
 * Services for saving and loading Diagram objects. The files are encoded
 * in UTF-8.
 */
public final class PersistenceService
{
	private PersistenceService() {}
	
	/**
     * Saves the current diagram in a file. 
     * 
     * @param pDiagram The diagram to save
     * @param pFile The file in which to save the diagram
     * @throws IOException If there is a problem writing to pFile.
     * @pre pDiagram != null.
     * @pre pFile != null.
     */
	public static void save(Diagram pDiagram, File pFile) throws IOException
	{
		assert pDiagram != null && pFile != null;
//		try( PrintWriter out = new PrintWriter(
//				new OutputStreamWriter(new FileOutputStream(pFile), StandardCharsets.UTF_8)))
//		{
//			out.println(JsonEncoder.encode(pDiagram).toString());
//		}
		XmlEncoder.encode(pDiagram, pFile);
	}
	
	/**
	 * Reads a diagram from a file.
	 * 
	 * @param pFile The file to read the diagram from.
	 * @return The diagram that is read in
	 * @throws IOException if the diagram cannot be read.
	 * @throws DeserializationException if there is a problem decoding the file.
	 * @pre pFile != null
	 */
	public static VersionedDiagram read(File pFile)
	{
		assert pFile != null;
		return new VersionedDiagram(XmlDecoder.decode(pFile), Version.create(3, 3, 3), false);
	}
}
