package me.caomr.utils;

import com.samskivert.mustache.Mustache;
import me.caomr.entity.FileItem;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPOutputStream;


public class FileUtil {

    public static final String indexTmpl = "index.tpl";
    private static final String defaultType = "application/octet-stream";
    public static final String HTML_CONTENT = "text/html";

    public static Map<String, String> fileMap = new HashMap<>();
    public static String template = "";


    static {
        fileMap = FileUtil.fileMap("mime.types");
        try {
            template = file2String(indexTmpl);
        } catch (IOException e) {

        }
    }

    public static Map<String, String> fileMap(String path) {
        InputStream ins = FileUtil.class.getClassLoader().getResourceAsStream(path);
        Map<String, String> map = new HashMap<String, String>();
        try (BufferedReader bis = new BufferedReader(new InputStreamReader(ins))) {
            String line = null;
            while ((line = bis.readLine()) != null) {
                String[] tmp = line.split("\\s+");
                map.put(tmp[0], tmp[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String getExtension(String name) {
        int index = name.lastIndexOf('.');
        if (index != -1)
            return name.substring(index + 1).toLowerCase();
        else
            return "";
    }

    public static String contentType(String extension) {
        String ext = getExtension(extension);
        return fileMap.getOrDefault(ext, defaultType);
    }

    /**
     * @param file the absolute file path
     * @param zip  gzip or not
     * @return byte array of the file
     * @throws IOException
     */
    public static byte[] file2ByteArray(File file, boolean zip)
            throws IOException {
        if (file.isFile()) {
            InputStream is = null;
            GZIPOutputStream gzip = null;
            byte[] buffer = new byte[8912];
            ByteArrayOutputStream baos = new ByteArrayOutputStream(8912);
            try {
                if (zip) {
                    gzip = new GZIPOutputStream(baos);
                }
                is = new BufferedInputStream(new FileInputStream(file));
                int read = 0;
                while ((read = is.read(buffer)) != -1) {
                    if (zip) {
                        gzip.write(buffer, 0, read);
                    } else {
                        baos.write(buffer, 0, read);
                    }
                }
            } catch (IOException e) {
                throw e;
            } finally {
                closeQuietly(is);
                closeQuietly(gzip);
            }
            return baos.toByteArray();
        } else if (file.isDirectory()) {
            return directoryList(file, zip);
        } else {
            return new byte[]{};
        }
    }


    /**
     * @param path
     * @return
     * @throws IOException
     */
    public static String file2String(String path) throws IOException {
        StringBuilder sb = new StringBuilder(300);
        InputStream ins = FileUtil.class.getClassLoader().getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(ins));
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
        }
        return sb.toString();
    }


    public static byte[] directoryList(File dir, boolean zip) {
        String html = Mustache.compiler().compile(template).execute(listDir(dir));
        if (zip) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(8912);
                GZIPOutputStream gzip = new GZIPOutputStream(baos);
                gzip.write(html.getBytes());
                closeQuietly(gzip);
                return baos.toByteArray();
            } catch (IOException e) {
            }
        } else {
            return html.getBytes();
        }
        return new byte[]{};
    }

    public static void closeQuietly(Closeable is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
    }


    public static Object listDir(final File folder) {
        File[] files = folder.listFiles();
        final List<FileItem> fileItems = new ArrayList<FileItem>();
        for (File file : files) {
            String href = file.isDirectory() ? file.getName() + "/" : file.getName();
            DateFormat df = new SimpleDateFormat("yyyy-HH-dd HH:mm:ss");
            String mtime = df.format(new Date(file.lastModified()));
            fileItems.add(new FileItem(href, file.getName(), file.length() + "", mtime));
        }
        Collections.sort(fileItems);
        return new Object() {
            Object files = fileItems;
            Object dir = folder.getName();
        };
    }
}
