package ge.ai.domino.server.dao.sysparam;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SystemParameter;
import ge.ai.domino.domain.sysparam.SystemParameterType;
import ge.ai.domino.server.dao.DatabaseUtil;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SystemParameterDAOImpl implements SystemParameterDAO {

    private Logger logger = Logger.getLogger(SystemParameterDAOImpl.class);

    private PreparedStatement pstmt;

    @Override
    public void addSystemParameter(SystemParameter systemParameter) throws DAIException {
        try {
            logger.info("Start addSystemParameter method");
            String uniqueSQL = "SELECT COUNT(*) FROM system_parameter WHERE key = ?";
            pstmt = DatabaseUtil.getConnection().prepareStatement(uniqueSQL);
            pstmt.setString(1, systemParameter.getKey());
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                logger.warn("System parameter with key [" + systemParameter.getKey() + "] already exists");
                throw new DAIException("keyMustBeUnique");
            }
            String sql = "INSERT INTO system_parameter (key, value, type) VALUES (?,?,?);";
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            pstmt.setString(1, systemParameter.getKey());
            pstmt.setString(2, systemParameter.getValue());
            pstmt.setString(3, systemParameter.getType() == null ? "CONTROL_PANEL" : systemParameter.getType().name());
            pstmt.executeUpdate();
            logger.info("Added system parameter with key [" + systemParameter.getKey() + "]");
        } catch (SQLException ex) {
            logger.error("Error occurred while add system parameter", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
    }

    @Override
    public void editSystemParameter(SystemParameter systemParameter) throws DAIException {
        try {
            logger.info("Start editSystemParameter method");
            String sql = "UPDATE system_parameter SET value = ?, type = ? WHERE key = ?";
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            pstmt.setString(1, systemParameter.getValue());
            pstmt.setString(2, systemParameter.getType().name());
            pstmt.setString(3, systemParameter.getKey());
            int count = pstmt.executeUpdate();
            if (count < 1) {
                logger.warn("System parameter with key [" + systemParameter.getKey() + "] don't exists");
                throw new DAIException("keyNotExist");
            }
            logger.info("Edited system parameter with key [" + systemParameter.getKey() + "]");
        } catch (SQLException ex) {
            logger.error("Error occurred while edit system parameter with key [" + systemParameter.getKey() + "]", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
    }

    @Override
    public void deleteSystemParameter(String key) throws DAIException {
        try {
            logger.info("Started deleteSystemParameter with key [" + key + "]");
            String sql = "DELETE FROM system_parameter WHERE key = ?";
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            pstmt.setString(1, key);
            int count = pstmt.executeUpdate();
            if (count < 1) {
                logger.warn("System parameter with key [" + key + "] don't exists");
                throw new DAIException("keyNotExist");
            }
            logger.info("Deleted system parameter with key [" + key + "]");
        } catch (SQLException ex) {
            logger.error("Error occurred while delete system parameter with key [" + key + "]", ex);
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
            logger.error("Error occurred while getting system parameters", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
        return systemParameterList;
    }
}
