package ge.ai.domino.server.dao.sysparam;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SystemParameter;
import ge.ai.domino.domain.sysparam.SystemParameterType;
import ge.ai.domino.server.dao.connection.ConnectionUtil;
import ge.ai.domino.server.dao.query.FilterCondition;
import ge.ai.domino.server.dao.query.QueryUtil;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SystemParameterDAOImpl implements SystemParameterDAO {

    private Logger logger = Logger.getLogger(SystemParameterDAOImpl.class);

    private static final String SYSTEM_PARAMETER_TABLE_NAME = "system_parameter";

    private static final String ID_COLUMN_NAME = "id";

    private static final String KEY_COLUMN_NAME = "key";

    private static final String VALUE_COLUMN_NAME = "value";

    private static final String TYPE_COLUMN_NAME = "type";

    private PreparedStatement pstmt;

    @Override
    public void addSystemParameter(SystemParameter systemParameter) throws DAIException {
        try {
            logger.info("Start addSystemParameter method");
            String uniqueSQL = String.format("SELECT COUNT(*) FROM %s WHERE key = ?", SYSTEM_PARAMETER_TABLE_NAME);
            pstmt = ConnectionUtil.getConnection().prepareStatement(uniqueSQL);
            pstmt.setString(1, systemParameter.getKey());
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                logger.warn("System parameter with key [" + systemParameter.getKey() + "] already exists");
                throw new DAIException("keyMustBeUnique");
            }
            String sql = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?,?,?);", SYSTEM_PARAMETER_TABLE_NAME, KEY_COLUMN_NAME, VALUE_COLUMN_NAME, TYPE_COLUMN_NAME);
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
            pstmt.setString(1, systemParameter.getKey());
            pstmt.setString(2, systemParameter.getValue());
            pstmt.setString(3, systemParameter.getType() == null ? SystemParameterType.CONSOLE_PARAMETER.name() : systemParameter.getType().name());
            pstmt.executeUpdate();
            logger.info("Added system parameter with key [" + systemParameter.getKey() + "]");
        } catch (SQLException ex) {
            logger.error("Error occurred while add system parameter", ex);
        } finally {
            ConnectionUtil.closeConnection();
        }
    }

    @Override
    public void editSystemParameter(SystemParameter systemParameter) throws DAIException {
        try {
            logger.info("Start editSystemParameter method");
            String sql = String.format("UPDATE %s SET %s = ?, %s = ? WHERE %s = ?", SYSTEM_PARAMETER_TABLE_NAME, VALUE_COLUMN_NAME, TYPE_COLUMN_NAME, KEY_COLUMN_NAME);
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
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
            ConnectionUtil.closeConnection();
        }
    }

    @Override
    public void deleteSystemParameter(String key) throws DAIException {
        try {
            logger.info("Started deleteSystemParameter with key [" + key + "]");
            String sql = String.format("DELETE FROM %s WHERE %s = ?", SYSTEM_PARAMETER_TABLE_NAME, KEY_COLUMN_NAME);
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
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
            ConnectionUtil.closeConnection();
        }
    }

    @Override
    public List<SystemParameter> getSystemParameters(String key, SystemParameterType type) {
        List<SystemParameter> systemParameterList = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder(String.format("SELECT * FROM %s WHERE 1 = 1 ", SYSTEM_PARAMETER_TABLE_NAME));
            if (key != null && !key.isEmpty()) {
                QueryUtil.addFilter(sql, KEY_COLUMN_NAME, key, FilterCondition.LIKE, true);
            }
            if (type != null) {
                QueryUtil.addFilter(sql, TYPE_COLUMN_NAME, type.name(), FilterCondition.EQUAL, true);
            }
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(ID_COLUMN_NAME);
                String k = rs.getString(KEY_COLUMN_NAME);
                String value = rs.getString(VALUE_COLUMN_NAME);
                String typeString = rs.getString(TYPE_COLUMN_NAME);
                SystemParameter systemParameter = new SystemParameter(id, k, value, SystemParameterType.valueOf(typeString));
                systemParameterList.add(systemParameter);
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting system parameters", ex);
        } finally {
            ConnectionUtil.closeConnection();
        }
        return systemParameterList;
    }
}
