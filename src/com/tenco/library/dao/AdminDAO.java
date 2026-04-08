package com.tenco.library.dao;

import com.tenco.library.dto.Admin;
import com.tenco.library.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    // 관리자 인증 (admin_id + password)
    public Admin authenAdmin(String adminId,String password) throws SQLException {

        String sql = """
                select * from admins where admin_id = ? and password = ?;
                """;

        try(Connection conn = DatabaseUtil.getConnection();
            PreparedStatement psmt = conn.prepareStatement(sql)
        ) {

            psmt.setString(1,adminId);
            psmt.setString(2,password);

            try (ResultSet rs = psmt.executeQuery()){

                if(rs.next()){
                    return Admin
                            .builder()
                            .id(rs.getInt("id"))
                            .adminId(rs.getString("admin_id"))
                            .name(rs.getString("name"))
                            .password(rs.getString("password"))
                            .build();
                    // tip. 인증후에는 일반적으로 비밀번호를 리턴하지 않습니다.
                }
            }

        }

        // TODO 반드시 Admin으로 리턴
        return null; // 인증 실패

    }

}
