package com.likya.tlossw.web.management;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.primefaces.component.datatable.DataTable;

import com.likya.tlos.model.xmlbeans.common.ActiveDocument.Active;
import com.likya.tlos.model.xmlbeans.common.RoleDocument.Role;
import com.likya.tlos.model.xmlbeans.common.UserIdDocument.UserId;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlos.model.xmlbeans.webservice.AllowedRolesDocument.AllowedRoles;
import com.likya.tlos.model.xmlbeans.webservice.AllowedUsersDocument.AllowedUsers;
import com.likya.tlos.model.xmlbeans.webservice.UserAccessProfileDocument.UserAccessProfile;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "wsAccessSearchPanelMBean")
@ViewScoped
public class WSAccessSearchPanelMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 1260229231620387952L;

	private UserAccessProfile userAccessProfile;

	private ArrayList<UserAccessProfile> searchWSAccessList;
	private transient DataTable searchWSAccessTable;

	private List<UserAccessProfile> filteredWSAccessList;

	private String active;

	private Collection<SelectItem> roleList = null;
	private String role = null;

	private Collection<SelectItem> userList = null;
	private String user = null;

	public void dispose() {
		userAccessProfile = null;
		searchWSAccessList = null;
	}

	@PostConstruct
	public void init() {
		userAccessProfile = UserAccessProfile.Factory.newInstance();
		searchWSAccessList = new ArrayList<UserAccessProfile>();

		ArrayList<Person> dbUserList = getDbOperations().getUsers();
		userList = WebInputUtils.fillUserList(dbUserList);

		roleList = WebInputUtils.fillRoleList();
	}

	public String getWSAccessProfileXML() {
		QName qName = UserAccessProfile.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String userAccessProfileXML = userAccessProfile.xmlText(xmlOptions);

		return userAccessProfileXML;
	}

	public void resetWSAccessProfileAction() {
		userAccessProfile = UserAccessProfile.Factory.newInstance();

		searchWSAccessList = new ArrayList<UserAccessProfile>();
		role = "";
		user = "";
		active = "";
	}

	public void searchWSAccessAction(ActionEvent e) {
		AllowedRoles allowedRoles = AllowedRoles.Factory.newInstance();
		if (!role.equals("")) {
			Role allowedRole = allowedRoles.addNewRole();
			allowedRole.setStringValue(role);
		}

		userAccessProfile.setAllowedRoles(allowedRoles);

		AllowedUsers allowedUsers = AllowedUsers.Factory.newInstance();
		if (!user.equals("")) {
			UserId userId = allowedUsers.addNewUserId();
			userId.setStringValue(user);
		}

		userAccessProfile.setAllowedUsers(allowedUsers);

		if (!active.equals("")) {
			userAccessProfile.setActive(Active.Enum.forString(active));
		} else {
			userAccessProfile.setActive(null);
		}

		searchWSAccessList = getDbOperations().searchWSAccessProfiles(getWSAccessProfileXML());
		if (searchWSAccessList == null || searchWSAccessList.size() == 0) {
			addMessage("searchWSAccessProfile", FacesMessage.SEVERITY_INFO, "tlos.info.search.noRecord", null);
		}
	}

	public void deleteWSAccessAction(ActionEvent e) {

	}

	public UserAccessProfile getUserAccessProfile() {
		return userAccessProfile;
	}

	public void setUserAccessProfile(UserAccessProfile userAccessProfile) {
		this.userAccessProfile = userAccessProfile;
	}

	public ArrayList<UserAccessProfile> getSearchWSAccessList() {
		return searchWSAccessList;
	}

	public void setSearchWSAccessList(ArrayList<UserAccessProfile> searchWSAccessList) {
		this.searchWSAccessList = searchWSAccessList;
	}

	public DataTable getSearchWSAccessTable() {
		return searchWSAccessTable;
	}

	public void setSearchWSAccessTable(DataTable searchWSAccessTable) {
		this.searchWSAccessTable = searchWSAccessTable;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Collection<SelectItem> getRoleList() {
		return roleList;
	}

	public void setRoleList(Collection<SelectItem> roleList) {
		this.roleList = roleList;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Collection<SelectItem> getUserList() {
		return userList;
	}

	public void setUserList(Collection<SelectItem> userList) {
		this.userList = userList;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public List<UserAccessProfile> getFilteredWSAccessList() {
		return filteredWSAccessList;
	}

	public void setFilteredWSAccessList(List<UserAccessProfile> filteredWSAccessList) {
		this.filteredWSAccessList = filteredWSAccessList;
	}

}
