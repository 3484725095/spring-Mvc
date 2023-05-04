package com.nf.mvc;

import com.nf.mvc.view.PlainViewResult;
import com.nf.mvc.view.VoidViewResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ViewResult {
    public abstract void render(HttpServletRequest req, HttpServletResponse resp) throws Exception;

    public static ViewResult handleViewResult(Object handlerResult) {
        ViewResult viewResult;
        if (handlerResult == null) {
            //这种情况表示handler方法执行返回null或者方法的签名本身就是返回void
            viewResult = new VoidViewResult();
        } else if (handlerResult instanceof ViewResult) {
            viewResult = (ViewResult) handlerResult;
        } else {
            viewResult = new PlainViewResult(handlerResult.toString());
        }

        return viewResult;
    }
}
