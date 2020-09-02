package org.javaboy.fileupload;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class FileUploadController {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("/yyyy/MM/dd/");

    @PostMapping("/upload")
    public Map<String, Object> fileUpload(MultipartFile file, HttpServletRequest req) {
        Map<String, Object> resultMap = new HashMap<>();

        // 得到上传时的文件名
        String originName = file.getOriginalFilename();
        if (!originName.endsWith(".pdf")) {
            resultMap.put("status", "error");
            resultMap.put("msg", "文件类型不对，必须为pdf");

            return resultMap;
        }

        String strFormat = simpleDateFormat.format(new Date());
        String realPath = req.getServletContext().getRealPath("/") + strFormat;
        String uploadDir = req.getSession().getServletContext().getRealPath("/") + "/upload/" + strFormat;

        File folder = new File(realPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 保存文件对象，加上uuid是为了防止文件重名
        String strNewFileName = UUID.randomUUID().toString().replaceAll("-", "") + ".pdf";
        try {
            file.transferTo(new File(folder, strNewFileName));
            String strUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + strFormat + strNewFileName;
            resultMap.put("status", "success");
            resultMap.put("url", strUrl);
        } catch (IOException e) {
            e.printStackTrace();

            resultMap.put("status", "error");
            resultMap.put("msg", e.getMessage());
        }

        return resultMap;
    }
}
