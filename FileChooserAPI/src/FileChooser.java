



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Pattern;



/**
 * FileChooser is a class is used for manipulating and validating files.
 * <p>
 *
 * @author d.galabov
 */


public class FileChooser
{

    private static String regexFullPath = "([\\w]:?)(\\[\\w]+)+\\.(txt|gif|pdf|doc|docx|xls|xlsx|jpg)";
    private static Pattern patternFullPath = Pattern.compile(regexFullPath);
    private Scanner scan = null;


    /**
     * Reading files for rename
     *
     * @param fileName - file name
     * @param fileNameTwo - file name
     * @return the name of the file
     */
    public boolean readPathForRename(String fileName, String fileNameTwo)
    {
        File filePath = null;
        File filePathTwo = null;
        try
        {
            scan = new Scanner(System.in);
            filePath = readFile(fileName, true);
            filePathTwo = readFile(fileNameTwo, false);
            if (!filePath.equals(filePathTwo))
            {
                renameFile(filePath, filePathTwo);
            }
            else
            {
                System.out.println("Files are same. Can not be renamed!");
                while (filePath.equals(filePathTwo))
                {
                    System.out.print("Enter file name two again: ");
                    fileNameTwo = scan.nextLine();
                    filePathTwo = readFile(fileNameTwo, false);
                }
                renameFile(filePath, filePathTwo);
            }
            return true;
        }
        finally
        {
            close(scan);
            scan = null;
        }

    }

    /**
     * Reading and validating file name
     *
     * @param fileName - file name
     * @param shouldExist - true if file exist, false - otherwise.
     * @return null if filePath doesn't exist and if it exists it will return filePath;
     */

    private File readFile(String fileName, boolean shouldExist)
    {
        scan = new Scanner(System.in);
        if (fileName != null)
        {
            File filePath = new File(fileName);

            while (!patternFullPath.matcher(fileName).matches())
            {
                System.out.printf("Wrong file name \"%s\"! Enter another: ", fileName);
                fileName = scan.nextLine();
            }
            filePath = new File(fileName);

            if (shouldExist)
            {
                while (!filePath.exists())
                {
                    System.out.printf("The file name \"%s\" does not exists! Enter another: ", fileName);
                    fileName = scan.nextLine();
                    filePath = new File(fileName);
                }
            }
            else
            {
                try
                {
                    filePath.createNewFile();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            return filePath;
        }
        return null;
    }


    /**
     * This method is used for reading and validating directory
     *
     * @param directory - directory name
     * @return true if directory is open; false otherwise.
     */
    private File readDirectory(String directory)
    {
        try
        {
            scan = new Scanner(System.in);
            if (directory != null)
            {
                File filePath = new File(directory);
                while (!filePath.isDirectory())
                {
                    System.out.printf("Wrong directory name \"%s\"! Enter another: ", directory);
                    directory = scan.nextLine();
                    filePath = new File(directory);
                }

                return filePath;
            }
        }
        finally
        {
            close(scan);
            scan = null;
        }

        return null;
    }


    /**
     * Copying two files
     *
     * @param fileName - file name one
     * @param fileNameTwo - file name two
     * @return filePathTwo
     */
    public boolean copy(String fileNameOne, String fileNameTwo)
    {
        scan = new Scanner(System.in);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try
        {
            File filePath = readFile(fileNameOne, true);
            File filePathTwo = readFile(fileNameTwo, false);


            fis = new FileInputStream(filePath);
            bis = new BufferedInputStream(fis);
            fos = new FileOutputStream(filePathTwo);
            bos = new BufferedOutputStream(fos);

            int length;
            while ((length = bis.read()) != -1)
            {
                bos.write(length);
            }
            System.out.println("Successfully copied!");
            return true;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            close(bis);
            close(bos);
            close(fis);
            close(fos);
            close(scan);
        }
        return false;

    }


    /**
     * Delete and validate file
     *
     * @param fileName - file name
     */
    public void readPathForDelete(String fileName)
    {
        scan = new Scanner(System.in);
        try
        {
            File filePath = readFile(fileName, false);

            if (filePath.exists())
            {
                deleteFile(filePath);
            }
            else
            {

                deleteFile(filePath);
            }
        }
        finally
        {
            close(scan);
            scan = null;
        }

    }


    /**
     * Delete a file
     *
     * @param filePath - filePath for delete
     */
    private boolean deleteFile(File filePath)
    {
        if (filePath != null)
        {
            if (filePath.delete())
            {
                System.out.println("Deleted!");
                return true;
            }
            else
            {
                System.out.println("Fail to delete!");
            }
        }
        return false;
    }


    /**
     * Renames file by pathname
     *
     * @param filePath - file name
     * @param filePathTwo - file name
     */
    private boolean renameFile(File filePath, File filePathTwo)
    {
        if (filePath != null && filePathTwo != null)
        {
            if (filePath.renameTo(filePathTwo))
            {
                System.out.println("Successfully renamed!");
                return true;
            }
            else
            {
                System.out.println("Fail to rename");
            }
        }

        return false;
        // Files.move(filePath.toPath(), filePathTwo.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }


    /**
     * Closes this stream and releases any system resources associated with it.
     *
     * @param out - stream to close
     */
    public static void close(Closeable out)
    {

        if (out != null)
        {
            try
            {
                out.close();
            }
            catch (Exception e)
            {
                // NOOP
            }
        }

    }


    /**
     * Searching and validating file name in directories
     *
     * @param fileName - file name
     * @param directory - directory
     * @return file name which is found in directory and if is not returns null
     */
    public File searchFileInDirectories(String fileName, String directory)
    {
        Pattern patt = Pattern.compile("^([a-zA-Z0-9_.-])+(.doc|.docx|.pdf|.txt|.jpg)$");
        try
        {
            scan = new Scanner(System.in);
            if (fileName != null)
            {
                while (!patt.matcher(fileName).matches())
                {
                    System.out.printf("Wrong file name \"%s\"! Enter another file name: ", fileName);
                    fileName = scan.nextLine();
                }
            }
            searchFileOrWord(new File(fileName), directory, null);
            // Predicate<File> check = f -> f != null;
        }
        finally
        {
            close(scan);
            scan = null;
        }
        return null;
    }


    /**
     * This method is inherited and it used for searching filename, directory or word depends on what is need for.
     *
     * @param fileName - input file name
     * @param directory - input directory
     * @param word - input word
     */
    private void searchFileOrWord(File fileName, String directory, String word)
    {

        if (fileName != null && directory != null)
        {
            File filePath = readDirectory(directory);
            Queue<File> queue = new LinkedList<>();
            queue.add(filePath);
            while (!queue.isEmpty())
            {
                File currentFile = queue.poll();
                File[] listOfDirectories = currentFile.listFiles();

                if (listOfDirectories != null)
                {
                    for (File file : listOfDirectories)
                    {
                        if (file.isDirectory())
                        {
                            queue.add(file);
                        }
                        else
                        {
                            if (word != null && !word.isEmpty())
                            {
                                readText(file, word);
                            }
                            else if (file.getName().equals(fileName.getName()))
                            {
                                System.out.println(file.getAbsolutePath() + " found");
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Searching word and prints the line where it is found in a file
     *
     * @param file - file path
     * @param word - word
     */
    private void readText(File file, String word)
    {
        try
        {

            scan = new Scanner(file);
            String line;
            int lineNumber = 0;
            while (scan.hasNextLine())
            {
                line = scan.nextLine();
                lineNumber++;
                if (line.contains(word))
                {
                    System.out.println("Line: " + lineNumber + " contains the word: " + word + "  at file: " + file.getAbsolutePath());
                }
            }

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        finally
        {
            close(scan);
            scan = null;
        }

    }


    /**
     * Searching word in parent directory and subdirectories using Breadth-First-Search algorithm.
     *
     * @param directory - directory name
     * @param word - word for search
     * @return return true if a word is found, false otherwise
     */

    public boolean searchWordInFiles(String directory, String word)
    {
        if ((directory != null && word != null) && !word.isEmpty())
        {
            searchFileOrWord(new File(directory), directory, word);
            return true;
        }
        return false;
    }


    

    /**
     * Filter files by regex
     *
     * @param directory - directory
     * @param input - regex to matching file
     * @return list of files found
     */
    public List<File> LikeFilter(String directory, String input)
    {
        List<File> listOfFiles = new ArrayList<File>();
        File filePath = null;
        Pattern pattern = Pattern.compile(input);
        if (directory != null)
        {
            filePath = readDirectory(directory);
            File[] files = filePath.listFiles(new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String name)
                {
                    return pattern.matcher(name).matches();
                }
            });

            if (files != null)
            {
                for (File file : files)
                {
                    listOfFiles.add(file);
                }
            }
        }
        return listOfFiles;

    }

    /**
     * @param path - path for read
     * @param charsets - array of charsets
     * @return List<String> if encoding is found in charsets; otherwise null
     * @throws IOException - if I/O exception occurs
     */
    private List<String> readAndCheckLines(Path path, Charset... charsets) throws IOException
    {
        if (path != null && charsets != null)
        {
            for (Charset cs : charsets)
            {
                try
                {
                    return Files.readAllLines(path, cs);
                }
                catch (MalformedInputException e)
                {
//                    String.format("dhfkjsdhfkds %s fdgdfgdf %s %d dfdfssds", "dfs", "dsfsdf", 12);

                    System.out.println("Wrong charset: " + cs + "! Trying with next one");
                }
            }
        }

        return null;
    }


    /**
     * Replacing text in specific line and specific charset
     *
     * @param fileName - file name
     * @param lineNumber - line number
     * @param content - content to replace the line
     * @return true if the file is successfully saved; false otherwise
     * @throws IOException - if an I/O error occurs reading from the file
     */

    public boolean replaceTextInSpecificLine(String fileName, int lineNumber, String content, Charset cs)
    {
        File filePath = readFile(fileName, true);
        List<String> lines = null;
        try
        {
            scan = new Scanner(System.in);
            if (filePath != null)
            {
                lines = readAndCheckLines(filePath.toPath(), cs, StandardCharsets.UTF_16, StandardCharsets.ISO_8859_1,
                                                             StandardCharsets.US_ASCII, StandardCharsets.UTF_16LE, StandardCharsets.UTF_16BE);
                if(filePath.length() >0 )
                {
                    if (lines != null)
                    {
                        while (lineNumber < 0 || lineNumber > lines.size() - 1)
                        {
                            System.out.print("Wrong line number or the file is empty! Enter another line: ");
                            lineNumber = scan.nextInt();
                            scan.nextLine();
                        }

                        lines.set(lineNumber - 1, content);
                        Files.write(filePath.toPath(), lines, cs);
                        System.out.println("Successfully saved!");

                    }
                    else
                    {
                        System.out.println("No charsets are found capable with this file!");
                    }
                }
                else
                {
                    System.out.println("File is empty");
                }

                return true;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            close(scan);
        }
        return false;
    }


    /**
     * Replacing text in specific line decoded into characters using the UTF-8 charset.
     *
     * @param fileName - file name
     * @param lineNumber - line number
     * @param content - content to replace the line
     * @return true if the file is successfully saved; false otherwise
     */
    public boolean replaceTextInSpecificLine(String fileName, int lineNumber, String content)
    {
        return replaceTextInSpecificLine(fileName, lineNumber, content, StandardCharsets.UTF_8);
    }

    /**
     * @param encoding - specify encoding for check
     * @return null if encoding not found
     */
    private String checkEncoding(String encoding)
    {
        scan = new Scanner(System.in);
        try
        {
            while (!Charset.availableCharsets().keySet().contains(encoding))
            {
                System.out.println("Wrong encoding! Enter another: ");
                encoding = scan.nextLine();
            }
            return encoding;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            close(scan);
        }
        return null;
    }


    /**
     * Method used for add text at the end of the file by specified encoding.
     *
     * @param fileName - file name
     * @param content - content to add at the end
     * @param encoding - encoding with specified encoding. If the encoding is null the default encoding is determined during virtual-machine
     *            startup.
     * @return true if the text is successfully added , false otherwise
     */
    public boolean AddTextAtTheEndOfFile(String fileName, String content, String encoding)
    {

        FileOutputStream fs = null;
        OutputStreamWriter ow = null;
        BufferedWriter bwriter = null;

        try
        {
            encoding = checkEncoding(encoding);
            File filePath = readFile(fileName, true);
            fs = new FileOutputStream(filePath, true);
            ow = new OutputStreamWriter(fs, encoding);

            bwriter = new BufferedWriter(ow);
            bwriter.write(content);
            System.out.println("Successfully add!");

            return true;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

            close(bwriter);
            close(ow);
            close(fs);
        }
        return false;
    }


    /**
     * Method used for add text at the end of the file by default system encoding.
     *
     * @param fileName - file name
     * @param content - content to add at the end
     * @return true if the text is successfully added , false otherwise
     */
    public boolean AddTextAtTheEndOfFile(String fileName, String content)
    {

        return AddTextAtTheEndOfFile(fileName, content, System.getProperty("file.encoding"));
    }


    /**
     * Save object to file(serialization)
     *
     * @param fileName - file name where we want to save
     * @param obj - object name to serialize
     * @return true if object isn't null, false otherwise
     * @throws NotSerializableException - if specific object doesn't implements Serializable or Externalizable interface
     */
    public boolean saveObjectToFile(String fileName, Object obj) throws NotSerializableException
    {
        ObjectOutputStream oos = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;

        try
        {
            File filePath = readFile(fileName, false);
            // filePath.createNewFile();

            fos = new FileOutputStream(filePath);
            bos = new BufferedOutputStream(fos);
            oos = new ObjectOutputStream(bos);

            if (obj != null)
            {
                oos.writeObject(obj);
                System.out.println("Successful save!");
                return true;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            close(oos);
            close(bos);
            close(fos);
        }

        System.out.println("Object can not be saved");
        return false;
    }


    /**
     * Load object from file after serialization
     *
     * @param fileName - file name from where we want to load object
     * @param obj - object
     * @return object if the files is loaded successfully , otherwise null
     * @throws StreamCorruptedException if none object is serialized in the file or violates internal consistency checks.
     * @throws EOFException - thrown when file is empty and non object can't be loaded.
     */

    public Object loadObjectFromFile(String fileName) throws StreamCorruptedException, EOFException
    {

        ObjectInputStream ois = null;
        BufferedInputStream bis = null;
        FileInputStream fis = null;

        try
        {
            File filePath = readFile(fileName, true);

            fis = new FileInputStream(filePath);
            bis = new BufferedInputStream(fis);
            ois = new ObjectInputStream(bis);

            Object obj = ois.readObject();
            System.out.println(obj.toString());
            return obj;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            close(ois);
            close(bis);
            close(fis);
        }

        System.out.println("Object can not be loaded from file");
        return null;
    }
}
