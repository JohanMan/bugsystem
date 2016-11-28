package com.dc.ddureportreceiver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.dc.ddureportreceiver.Log;

public class LogDao {
	
	public boolean insertLog(Log log) {
		if (log == null) {
			return false;
		}
		String insertMysql = "insert into log(l_time,l_log_path,l_state) values(?,?,?)";
		try {
			Connection connection = DBConnectionManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(insertMysql);
			preparedStatement.setString(1, log.getTime());
			preparedStatement.setString(2, log.getLogPath());
			preparedStatement.setInt(3, log.getState());
			int effectRow = preparedStatement.executeUpdate();
			return effectRow == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean updateLog(Log log) {
		if (log == null) {
			return false;
		}
		String updateMysql = "update log set l_state=? where l_id=?";
		try {
			Connection connection = DBConnectionManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(updateMysql);
			preparedStatement.setInt(1, log.getState());
			preparedStatement.setInt(2, log.getId());
			int effectRow = preparedStatement.executeUpdate();
			return effectRow > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<Log> findLogList() {
		List<Log> logList = new ArrayList<Log>();
		String findMysql = "select * from log order by l_state asc, l_time desc";
		try {
			Connection connection = DBConnectionManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(findMysql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				int id = resultSet.getInt(1);
				String time = resultSet.getString(2);
				String logPath = resultSet.getString(3);
				int state = resultSet.getInt(4);
				Log log = new Log(id, time, logPath, state);
				logList.add(log);
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return logList;
	}

}
