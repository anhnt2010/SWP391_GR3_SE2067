/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.StoreDAO;
import dao.UserDAO;
import model.Store;
import model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AssignManagerController", urlPatterns = {"/AssignManagerController"})
public class AssignManagerController extends HttpServlet {

    // chuẩn bị danh sách và mở giao diện
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Gọi DAO lấy danh sách
        List<Store> listStores = new StoreDAO().getAllStores();
        List<User> listUsers = new UserDAO().getEligibleManagers();

        // Đính kèm vào request
        request.setAttribute("listStores", listStores);
        request.setAttribute("listUsers", listUsers);

        // Chuyển hướng sang giao diện
        request.getRequestDispatcher("assign_manager.jsp").forward(request, response);
    }

    // Hàm nhận dữ liệu khi Giám đốc bấm nút "Xác nhận Phân công"
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // (Phần này sẽ gọi hàm assignManager() trong UserDAO để lưu vào Database)
    }
}
