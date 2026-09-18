/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.StoreDAO;
import model.Store;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author nguyn
 */
public class StoreController extends HttpServlet { 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chuyển hướng người dùng đến trang giao diện thêm cơ sở
        request.getRequestDispatcher("add_store.jsp").forward(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Cấu hình UTF-8 để không bị lỗi font tiếng Việt khi nhập liệu
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // 2. Lấy dữ liệu từ các ô input của Form (dựa vào thuộc tính name)
        String storeName = request.getParameter("txtStoreName");
        String address = request.getParameter("txtAddress");
        
        // 3. Đóng gói dữ liệu vào đối tượng Model (store_id truyền tạm bằng 0 vì Database tự tăng)
        Store newStore = new Store(0, storeName, address);
        
        // 4. Gọi DAO để thực hiện câu lệnh INSERT
        StoreDAO dao = new StoreDAO();
        boolean isSuccess = dao.insertStore(newStore);
        
        // 5. Xử lý điều hướng sau khi lưu xong
        if (isSuccess) {
            // Nếu thành công: Báo thành công và hiển thị lại form trống
            request.setAttribute("message", "Đã khai báo cơ sở mới thành công!");
        } else {
            // Nếu thất bại: Báo lỗi
            request.setAttribute("error", "Lỗi hệ thống. Khai báo thất bại!");
        }
        
        // Điều hướng kết quả về lại trang giao diện
        request.getRequestDispatcher("add_store.jsp").forward(request, response);
    }

}
