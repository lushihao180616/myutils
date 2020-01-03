package com.lushihao.myutils.file;

import com.lushihao.myutils.check.LSHCheckUtils;
import com.lushihao.myutils.file.vo.FTPVo;
import com.lushihao.myutils.file.vo.FileAttr;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LSHFTPUtils {
    /**
     * 编码格式
     */
    public final String UTF8 = "UTF-8";
    /**
     * 编码格式
     */
    public final String GBK = "GBK";
    /**
     * ftp客户端连接类
     */
    private FTPClient client;
    /**
     * ftp客户端的连接信息
     */
    private FTPVo vo;

    public LSHFTPUtils(FTPVo vo) {
        this.vo = vo;
        client = createFTPClien(vo);
    }

    /**
     * 创建变连接FTP
     *
     * @param vo
     * @return
     */
    private FTPClient createFTPClien(FTPVo vo) {
        FTPClient client = new FTPClient();
        int reply = -1;
        try {
            client.connect(vo.getHostName(), vo.getPort());
            client.login(vo.getUsername(), vo.getPassword());
            reply = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                client.disconnect();
                return null;
            } else {
                client.setControlEncoding(vo.getRemoteEncoding());
                client.setFileType(FTPClient.BINARY_FILE_TYPE);
                if (vo.isPassiveMode()) {
                    client.enterLocalPassiveMode();
                } else {
                    client.enterRemotePassiveMode();
                }
                client.cwd(vo.getRemoteDir());
            }
            return client;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过FTP响应码判断是否操作成功
     *
     * @return
     */
    public boolean reply() {
        int replyCode = client.getReplyCode();
        return FTPReply.isPositiveCompletion(replyCode);
    }

    /**
     * 是否存在远程文件
     *
     * @param fileName
     * @return
     */
    public boolean isExists(String fileName) {
        List<String> list = listFile(vo.getRemoteDir());
        if (list.contains(fileName)) {
            return true;
        }
        return false;
    }

    /**
     * 下载文件
     *
     * @param fileName
     * @return
     */
    public boolean downLoad(String fileName) {
        String localfileName = vo.getLocalDir() + File.separator + fileName;
        LSHIOUtils.createFiles(localfileName);
        OutputStream out = null;
        try {
            out = new FileOutputStream(localfileName, true);
            client.retrieveFile(new String(fileName.getBytes(vo.getRemoteEncoding()), "ISO-8859-1"), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return reply();
    }

    /**
     * 下载文档
     *
     * @param directory
     * @return
     */
    public boolean downLoadDir(String directory) {
        List<String> files = listFile(directory);
        for (String s : files) {
            downLoad(s);
        }
        return true;
    }

    /**
     * 删除文件
     *
     * @param fileName
     * @return
     */
    public boolean deleteFile(String fileName) {
        if (isExists(fileName)) {
            try {
                client.deleteFile(new String(fileName.getBytes(vo.getRemoteEncoding()), "ISO-8859-1"));
                return reply();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 删除目录
     *
     * @param directory
     * @return
     */
    public boolean deleteDir(String directory) {
        List<String> files = listFile(directory);
        try {
            for (String s : files) {
                deleteFile(s);
            }
            List<String> dirs = listDir(directory);
            for (int i = dirs.size() - 1; i >= 0; i--) {
                client.removeDirectory(new String(dirs.get(i).getBytes(vo.getRemoteEncoding()), "ISO-8859-1"));
            }
            client.removeDirectory(new String(directory.getBytes(vo.getRemoteEncoding()), "ISO-8859-1"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reply();
    }

    /**
     * 添加文件
     *
     * @param file
     * @param remoteFileName
     * @param isDelete       true，删除后新建；false，新建
     * @return
     */
    public boolean putFile(File file, String remoteFileName, boolean isDelete) {
        String remoteFile = remoteFileName;
        String path = "";
        if (remoteFileName.lastIndexOf("/") != -1) {
            path = remoteFileName.substring(0, remoteFileName.lastIndexOf("/"));
            remoteFile = remoteFileName.substring(remoteFileName.lastIndexOf("/") + 1);
            mkDir(path);
            changeWorkDir(path);
        }
        try (InputStream in = new FileInputStream(file)) {
            if (isDelete) {
                deleteFile(new String(file.getName().getBytes(vo.getRemoteEncoding()), "ISO-8859-1"));
            }
            client.appendFile(new String(remoteFile.getBytes(vo.getRemoteEncoding()), "ISO-8859-1"), in);
            return reply();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加文件夹
     *
     * @param file
     * @param remoteDir
     * @return
     */
    public boolean putDir(File file, String remoteDir) {
        List<File> list = LSHIOUtils.listFile(file);
        for (File f : list) {
            String name = f.getAbsolutePath();
            name = name.substring(name.indexOf(file.getName())).replaceAll("\\\\", "/");
            putFile(f, remoteDir + "/" + name, true);
        }
        return true;
    }

    /**
     * 获取文件夹的文件列表
     *
     * @param directory
     * @return
     */
    public List<String> listFile(String directory) {
        List<String> list = new ArrayList<String>();
        try {
            FTPFile[] files = client.listFiles(directory);
            for (int i = 0; i < files.length; i++) {
                String t = (directory + "/" + files[i].getName()).replaceAll("//", "/");
                if (files[i].isFile()) {
                    list.add(t);
                } else if (files[i].isDirectory()) {
                    list.addAll(listFile((t + "/").replaceAll("//", "/")));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取文件夹的文件属性列表
     *
     * @param directory
     * @return
     */
    public Map<String, FileAttr> listFileAttr(String directory) {
        Map<String, FileAttr> map = new HashMap<String, FileAttr>();
        try {
            FTPFile[] files = client.listFiles(directory);
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    FTPFile file = files[i];
                    String fileName = directory + file.getName();
                    FileAttr attr = new FileAttr();
                    attr.setFileName(fileName);
                    attr.setModifyTime(file.getTimestamp().getTime());
                    attr.setSize(file.getSize());
                    map.put(fileName, attr);
                } else if (files[i].isDirectory()) {
                    map.putAll(listFileAttr(directory + files[i].getName() + "/"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 修改当前工作目录
     *
     * @param directory
     * @return
     */
    public boolean changeWorkDir(String directory) {
        try {
            client.cwd(directory);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前工作目录
     *
     * @return
     */
    public String getWorkDir() {
        try {
            return client.printWorkingDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 创建文件夹
     *
     * @param directory
     * @return
     */
    public boolean mkDir(String directory) {
        directory = directory.replaceAll("//", "/");
        if (directory.startsWith("/")) {
            directory = directory.substring(1);
        }
        if (directory.endsWith("/")) {
            directory = directory.substring(0, directory.length() - 1);
        }
        try {
            String[] str = (new String(directory.getBytes(vo.getRemoteEncoding()), "ISO-8859-1")).split("/");
            String t = "";
            String parnet = "";
            for (int i = 0; i < str.length; i++) {
                t += ("/" + str[i]);
                if (!isExists(t.substring(1))) {
                    client.makeDirectory(str[i]);
                }
                client.changeWorkingDirectory(str[i]);
                parnet += "../";
            }
            if (str.length >= 1) {
                client.changeWorkingDirectory(parnet);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 展示当前文件夹下的文件夹
     *
     * @param directory
     * @return
     */
    public LinkedList<String> listDir(String directory) {
        LinkedList<String> list = new LinkedList<>();
        try {
            FTPFile[] files = client.listFiles(directory);
            for (int i = 0; i < files.length; i++) {
                String t = (directory + "/" + files[i].getName()).replaceAll("//", "/");
                if (files[i].isDirectory()) {
                    list.add(t);
                    list.addAll(listDir(t + "/"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取当前连接信息
     *
     * @return
     */
    public FTPClient getClient() {
        return client;
    }

    /**
     * 断开连接
     */
    public void destory() {
        if (LSHCheckUtils.valid(client)) {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取路径
     *
     * @param file
     * @return
     */
    public String getParentPath(String file) {
        if (file.indexOf("/") != -1) {
            String temp = null;
            Pattern p = Pattern.compile("[/]+");
            Matcher m = p.matcher(file);
            int i = 0;
            while (m.find()) {
                temp = m.group(0);
                i += temp.length();
            }
            String parent = "";
            for (int j = 0; j < i; j++) {
                parent += "../";
            }
            return parent;
        } else {
            return "./";
        }
    }

}