package com.lmz.servlet;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lmz.entity.User;
import com.lmz.service.UserService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * @Description 头像的上传
 */
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String MYPAGE_VIEW = "/jsp/alter.jsp";
    private static final String DRI= "/image/upload";
    /**
     * 调用service层方法
     */
    UserService userService = new UserService();
    User user = new User();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        DiskFileItemFactory sf= new DiskFileItemFactory();//实例化磁盘被文件列表工厂
        String path = request.getRealPath("/image/upload");//得到上传文件的存放目录
        sf.setRepository(new File(path));//设置文件存放目录
        sf.setSizeThreshold(1024*1024);//设置文件上传小于1M放在内存中
        String newName = "";//文件新生成的文件名
        String fileName = "";//文件原名称
        String name = "";//普通field字段
        //从工厂得到servletupload文件上传类
        ServletFileUpload sfu = new ServletFileUpload(sf);

        try {
            List<FileItem> lst = sfu.parseRequest(request);//得到request中所有的元素
            for (FileItem fileItem : lst) {
                if(fileItem.isFormField()){
                    if("name".equals(fileItem.getFieldName())){
                        name = fileItem.getString("UTF-8");
                    }
                }else{
                    //获得文件名称
                    fileName = fileItem.getName();
                    fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
                    String houzhui = fileName.substring(fileName.lastIndexOf("."));
                    newName = UUID.randomUUID()+houzhui;
                    System.out.println("upload--------imgurl---------" + user.getPortrait());
                    fileItem.write(new File(path, newName));
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //得到session
        HttpSession session = request.getSession();
        //获得session中的userInfo对象
        User userInfo = (User) session.getAttribute("userInfo");
        String newPath = DRI + File.separator+ newName;
        //图片地址放在user对象中
        user.setPortrait(newPath);
        user.setUsername((String) session.getAttribute("uname"));
        //user放在session
        session.setAttribute("user", user);
        //放置图片地址到session中
        session.setAttribute("portrait", newPath);
        userInfo.setPortrait(user.getPortrait());
        session.setAttribute("userInfo", userInfo);
        userService.portrait(userInfo);
//        response.sendRedirect(MYPAGE_VIEW);
        request.getRequestDispatcher(MYPAGE_VIEW).forward(request, response);
        out.flush();
        out.close();
    }

//    /**
//     * @param part filename(文件名)
//     * @return
//     * @Description 取得上传的文件名
//     */
//    private String getFileName(Part part) {
//        String header = part.getHeader("Content-Disposition");
//        String fileName =
//                header.substring(header.indexOf("filename=\"") + 10,
//                        header.lastIndexOf("\""));
//        return fileName;
//    }

}
