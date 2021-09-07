package coza.opencollab.sakai.api.dao;

import java.util.List;

import coza.opencollab.sakai.api.model.ITSUser;
/**
 * 
 * DAO class for Ghana ITS Users
 * 
 * @author JC Gillman
 *
 */
public interface ITSUserDao {

    public ITSUser updateITSUser(ITSUser user);
    
    public List<ITSUser> getITSUsers();
    
    public ITSUser getITSUserById(Long id);

    public ITSUser getITSUserByUserId(Long userId);

    public boolean deleteITSUser(Long id);

	public List<ITSUser> getITSUsersByAcadYear(int year);
}
