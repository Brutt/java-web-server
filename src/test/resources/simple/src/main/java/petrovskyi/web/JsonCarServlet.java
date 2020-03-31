package petrovskyi.web;

import com.google.gson.Gson;
import petrovskyi.entity.Car;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JsonCarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Car car = new Car("yellow", "renault");
        String carJsonString = new Gson().toJson(car);

        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.print(carJsonString);
        out.flush();
    }
}
