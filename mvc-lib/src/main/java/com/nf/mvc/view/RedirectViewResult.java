package com.nf.mvc.view;

import com.nf.mvc.ViewResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class RedirectViewResult extends ViewResult {
    private String url;
    private Map<String, Object> model;

    public RedirectViewResult(String url) {
        this(url, new HashMap<>());
    }

    public RedirectViewResult(String url, Map<String, Object> model) {
        this.url = url;
        this.model = model;
    }

    @Override
    public void render(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        this.url += initModel();
        resp.sendRedirect(url);
    }

    private String initModel() {
        if (model == null) return "";
        StringBuilder sb = new StringBuilder("?");
        boolean hasModel = false;
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            if (hasModel) {
                sb.append("&");
            } else {
                hasModel = true;
            }
            sb.append(entry.getKey() + "=");
            sb.append(entry.getValue());
        }

        return sb.toString();
    }
}
