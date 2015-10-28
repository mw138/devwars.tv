package com.bezman.storage;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.Files;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;

@Component
public class FileStorage
{
    @Autowired
    private Dropbox dropbox;

    public final String PROFILE_PICTURE_PATH = "/profilepics";

    public final String TEAM_PICTURE_PATH = "/teampics";

    public final String SITE_STORAGE_PATH = "/sitestorage";

    public Files.FileMetadata uploadFile(String path, InputStream inputStream) throws IOException, DbxException
    {
        try
        {
            dropbox.dbxClientV2.files.delete(path);
        }catch (DbxException e) {}

        return dropbox.dbxClientV2.files.uploadBuilder(path).run(inputStream);
    }

    public InputStream getFile(String path) throws DbxException
    {
        return getFileDownloader(path).body;
    }

    public DbxDownloader<Files.FileMetadata> getFileDownloader(String path) throws DbxException
    {
        return dropbox.dbxClientV2.files.downloadBuilder(path).start();
    }

    public Files.FileMetadata getMetaDataForPath(String path) throws DbxException
    {
        return (Files.FileMetadata) dropbox.dbxClientV2.files.getMetadata(path);
    }

    public ArrayList<Files.Metadata> getDirMetaDataForPath(String path) throws DbxException
    {
        return dropbox.dbxClientV2.files.listFolder(path).entries;
    }

    public String shareableUrlForPath(String path)
    {
        try
        {
            String url = dropbox.dbxClientV2.sharing.createSharedLink(path).url;
            url = url.split("\\?")[0];
            return url + "?raw=1";
        } catch (DbxException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
