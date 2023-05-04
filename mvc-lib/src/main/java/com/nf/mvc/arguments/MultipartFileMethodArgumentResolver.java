package com.nf.mvc.arguments;

import com.nf.mvc.MethodArgumentResolver;
import com.nf.mvc.file.MultipartFile;
import com.nf.mvc.file.StandardMultipartFile;
import com.nf.mvc.util.FileCopyUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MultipartFileMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supports(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        return isFileType(paramType)
                || (paramType.isArray() && isFileType(paramType.getComponentType()));
    }

    private boolean isFileType(Class<?> fileType) {
        return Part.class == fileType ||
                MultipartFile.class == fileType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HttpServletRequest request) throws Exception {
        Class<?> paramType = parameter.getParameterType();
        String paramName = parameter.getParamName();
        if (paramType.isArray()) {
            return handleMultiFile(request.getParts(), paramType.getComponentType());
        } else {
            return handleSingleFile(request.getPart(paramName), paramType);
        }

    }

    private <T> List<T> handleMultiFile(Collection<Part> parts, Class<T> fileType) throws IOException, ServletException {

        List<T> files = new ArrayList<>();
        for (Part part : parts) {
            handleSingleFile(part, fileType);
        }
        return files;
    }

    private <T> T handleSingleFile(Part part, Class<T> paramType) throws IOException, ServletException {

        if (Part.class == paramType) {
            return (T) part;
        } else {
            String disposition = part.getHeader(FileCopyUtils.CONTENT_DISPOSITION);
            String filename = getFileName(disposition);
            StandardMultipartFile multipartFile = new StandardMultipartFile(part, filename);
            return (T) multipartFile;
        }

    }

    private String getFileName(String disposition) {
        String fileName = disposition.substring(disposition.indexOf("filename=\"") + 10, disposition.lastIndexOf("\""));
        System.out.println(fileName);
        return fileName;
    }
}
