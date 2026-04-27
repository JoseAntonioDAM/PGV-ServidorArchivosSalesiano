package net.salesianos.common;

public class FileInfo {
    private String fileName;
    private long fileSize;
    private boolean exists;

    public FileInfo(String fileName, long fileSize, boolean exists) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.exists = exists;
    }

    public String getFileName() { return fileName; }
    public long getFileSize() { return fileSize; }
    public boolean isExists() { return exists; }

     @Override
    public String toString() {
        return "FileInfo{fileName='" + fileName + "', fileSize=" + fileSize + ", exists=" + exists + "}";
    }
}
