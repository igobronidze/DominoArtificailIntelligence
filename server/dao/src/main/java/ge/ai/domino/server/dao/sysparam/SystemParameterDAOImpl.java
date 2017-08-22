package ge.ai.domino.server.dao.sysparam;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SystemParameter;
import ge.ai.domino.domain.sysparam.SystemParameterType;
import ge.ai.domino.server.dao.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SystemParameterDAOImpl implements SystemParameterDAO {

    private PreparedStatement pstmt;

    @Override
    public void addSystemParameter(SystemParameter systemParameter) throws DAIException {
        try {
            String uniqueSQL = "SELECT COUNT(*) FROM system_parameter WHERE key = ?";
            pstmt = DatabaseUtil.getConnection().prepareStatement(uniqueSQL);
            pstmt.setString(1, systemParameter.getKey());
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                throw new DAIException("keyMustBeUnique");
            }
            String sql = "INSERT INTO system_parameter (key, value, type) VALUES (?,?,?);";
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            pstmt.setString(1, systemParameter.getKey());
            pstmt.setString(2, systemParameter.getValue());
            pstmt.setString(3, systemParameter.getType() == null ? "CONTROL_PANEL" : systemParameter.getType().name());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DatabaseUtil.closeConnection();
        }
    }

    @Override
    public void editSystemParameter(SystemParameter systemParameter) throws DAIException {
        try {
            String sql = "UPDATE system_parameter SET value = ?, type = ? WHERE key = ?";
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            pstmt.setString(1, systemParameter.getValue());
            pstmt.setString(2, systemParameter.getType().name());
            pstmt.setString(3, systemParameter.getKey());
            int count = pstmt.executeUpdate();
            if (count < 1) {
                throw new DAIException("keyNotExist");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DatabaseUtil.closeConnection();
        }
    }

    @Override
    public void deleteSystemParameter(String key) throws DAIException {
        try {
            String sql = "DELETE FROM system_parameter WHERE key = ?";
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            pstmt.setString(1, key);
            int count = pstmt.executeUpdate();
            if (count < 1) {
                throw new DAIException("keyNotExist");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DatabaseUtil.closeConnection();
        }
    }

    @Override
    public List<SystemParameter> getSystemParameters(String key, SystemParameterType type) {
        List<SystemParameter> systemParameterList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM system_parameter WHERE 1 = 1 ";
            if (key != null && !key.isEmpty()) {
                sql += "AND key LIKE '%" + key + "%' ";
            }
            if (type != null) {
                sql += "AND type = '" + type.name() + "' ";
            }
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String k = rs.getString("key");
                String value = rs.getString("value");
                String t = rs.getString("type");
                SystemParameter systemParameter = new SystemParameter(id, k, value, SystemParameterType.valueOf(t));
                systemParameterList.add(systemParameter);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DatabaseUtil.closeConnection();
        }
        return systemParameterList;
    }
}
