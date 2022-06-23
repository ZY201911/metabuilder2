package file;

import static resources.MetaBuilderResources.RESOURCES;

import java.io.File;

import javafx.stage.FileChooser.ExtensionFilter;

/**
 * A utility class to create and access diagram extension 
 * filters used by the file chooser.
 */
public final class FileExtensions
{
	private static final String EXTENSION_JET = ".xmi";
	
	private static ExtensionFilter FILTER = new ExtensionFilter(RESOURCES.getString("metamodeldiagram.file.name"),
			"*" + EXTENSION_JET);
	
	private FileExtensions() {}
	
	public static ExtensionFilter getFilter()
	{
		return FILTER;
	}
	
	/**
	 * @param pFile The file to clip, if applicable.
	 * @return A file with the same name as pFile, but with
	 *     the application extension removed. If there is no application
	 *     extension to clip, the same file object is returned.
	 * @pre pFile != null
	 */
	public static File clipApplicationExtension(File pFile)
	{
		assert pFile != null;
		if( !pFile.getAbsolutePath().endsWith(EXTENSION_JET))
		{
			return pFile;
		}
		return new File(pFile.getAbsolutePath()
				.substring(0, pFile.getAbsolutePath().length() - EXTENSION_JET.length()));
	}
}
	
	