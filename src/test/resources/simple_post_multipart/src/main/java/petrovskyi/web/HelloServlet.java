package petrovskyi.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class HelloServlet extends HttpServlet {
    private static final String UPLOAD_DIRECTORY = "upload";

    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 40;
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();

        writer.write("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<body>\n" +
                "<form action=\"hello\" method=\"post\" enctype=\"multipart/form-data\">\n" +
                "  <p><input type=\"file\" name=\"file1\">\n" +
                "  <p><input type=\"file\" name=\"file2\">\n" +
                "  <p><button type=\"submit\">Submit</button>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().removeAttribute("messages");
        if (ServletFileUpload.isMultipartContent(request)) {

            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(MEMORY_THRESHOLD);
            factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(MAX_FILE_SIZE);
            upload.setSizeMax(MAX_REQUEST_SIZE);
            File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
            String uploadPath = file.getParent() + File.separator + UPLOAD_DIRECTORY;
            System.out.println("!!!! => " + uploadPath);
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            List<String> values = new ArrayList<>();
            try {
                List<FileItem> formItems = upload.parseRequest(request);

                if (formItems != null && formItems.size() > 0) {

                    for (FileItem item : formItems) {
                        try {
                            if (!item.isFormField()) {
                                String fileName = new File(item.getName()).getName();
                                if(fileName.isEmpty()){
                                    continue;
                                }
                                String filePath = uploadPath + File.separator + fileName;
                                File storeFile = new File(filePath);
                                item.write(storeFile);
                                String value = "File " + fileName + " has uploaded successfully!";
                                values.add(value);
                            }
                        } catch (Exception e) {
                            String value = "There was an error: " + e.getMessage();
                            values.add(value);
                        }
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException("error:", ex);
            }
            request.getSession().setAttribute("messages", values);

            response.sendRedirect(request.getContextPath() + "/result");
        }
    }
}

