package com.nf.mvc.view;

import com.nf.mvc.ViewResult;
import com.nf.mvc.util.FileCopyUtils;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.http.Part;

public class FiveViewResult extends ViewResult {
    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";
    private String realPath;

    public FiveViewResult(String realPath) {
        this.realPath = realPath;
    }

    @Override
    public void render(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //文件下载
        String filename = getFileName();
        resp.setContentType(getMediaType(filename));
        resp.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
        Path path = Paths.get(realPath);
        FileCopyUtils.copy(Files.newInputStream(path), resp.getOutputStream());
    }

    private String getFileName() {
        int lastSlash = realPath.lastIndexOf("/");
        return realPath.substring(lastSlash + 1);
    }

    private String getMediaType(String fileName) {
        String midiaType = URLConnection.guessContentTypeFromName(fileName);
        if (midiaType == null) {
            midiaType = APPLICATION_OCTET_STREAM_VALUE;
        }
        return midiaType;
    }
}
