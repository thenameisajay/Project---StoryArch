package main.java.storyArch.service;


import main.java.storyArch.model.IllustrationServices;
import main.java.storyArch.model.Project;

import java.io.*;
import java.util.*;

/**
 * This class is used for storing the project information.
 * All logic related to the project is handled in this class.
 */
public class ProjectService implements Serializable {


    private Map<Integer, Project> projects = new HashMap<>();

    private List<Integer> projectIDS = new ArrayList<>();


    /**
     * This method is used for getting the project by the creator of the project.
     *
     * @param creator - The creator of the project.
     * @return - Returns a map of all the projects created by the user.
     */
    public Map<Integer, Project> getProjectByCreator(String creator) {
        Map<Integer, Project> projectsByCreator = new HashMap<>();
        for (Project project : projects.values()) {
            if (project.getCreator().equals(creator.toLowerCase())) {
                projectsByCreator.put(project.getProjectID(), project);
            }
        }
        return projectsByCreator;
    }

    /**
     * This method is used for creating a new project.
     *
     * @param projectName          - The name of the project.
     * @param projectDescription   - The description of the project.
     * @param creator              - The creator of the project.
     * @param date                 - The date of the project.
     * @param illustrationServices - The illustration services used in the project.
     * @param teamMembers          - The team members of the project.
     */
    public void createProject(String projectName, String projectDescription, String creator, Date date, IllustrationServices illustrationServices, List<String> teamMembers) {
        // first check for null values and throw an exception if any of the values are null.
        if (projectName == null || projectDescription == null || creator == null || date == null || illustrationServices == null) {
            throw new IllegalArgumentException("One or more of the values are null");
        }
        // Check if the project name already exists in the database.
        for (Project project : projects.values()) {
            if (project.getProjectName().equals(projectName.toLowerCase()) && project.getCreator().equals(creator.toLowerCase())) {
                throw new IllegalArgumentException("Project name already exists ! Create a new project name.");
            }
        }
        // Check if team members are  not creator
        if (teamMembers != null) {
            if (teamMembers.contains(creator.toLowerCase())) {
                throw new IllegalArgumentException("You cannot add yourself as a team member");
            }
        }

        // Create a random Project ID using numeric values of 7 digits
        int projectID = (int) (Math.random() * 10000000);
        projectIDS.add(projectID);
        while (projectIDS.contains(projectID)) {
            projectID = (int) (Math.random() * 10000000);
        }
        // Add the project to the database.
        projects.put(projectID, new Project(projectID, projectName.toLowerCase(), projectDescription, creator.toLowerCase(), date, illustrationServices, teamMembers));
    }

    /**
     * This method is used for saving the project data to a file.
     *
     * @throws IOException - If the file is not found
     */
    public void saveData() throws IOException {
        FileOutputStream f = new FileOutputStream("src/resources/projectData.ser");
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(projects);
        o.close();
        f.close();
    }

    /**
     * Load the user data from a file
     *
     * @throws IOException            - If the file is not found
     * @throws ClassNotFoundException - If the class is not found
     */
    public void loadData() throws IOException, ClassNotFoundException {
        FileInputStream fi = new FileInputStream("src/resources/projectData.ser");
        ObjectInputStream oi = new ObjectInputStream(fi);
        // Read objects
        projects = (Map<Integer, Project>) oi.readObject();
        oi.close();
        fi.close();
        // Take the key value (ProjectID) and add it to the arraylist
        for (Map.Entry<Integer, Project> entry : projects.entrySet()) {
            projectIDS.add(entry.getKey());
        }
    }


    /**
     * This method is used for viewing projects shared with the user.
     *
     * @param userName - The user-name of the user.
     * @return - Returns a map of all the projects shared with the user.
     */
    public Map<Integer, Project> getSharedProjects(String userName) {
        Map<Integer, Project> sharedProjects = new HashMap<>();
        for (Project project : projects.values()) {
            if (project.getTeamMembers() != null) {
                if (project.getTeamMembers().contains(userName.toLowerCase())) {
                    sharedProjects.put(project.getProjectID(), project);
                }
            }
        }
        return sharedProjects;
    }

    /**
     * This method is used for deleting the project by the project ID and the creator of the project.
     *
     * @param projectID - The project ID of the project.
     * @param creator   - The creator of the project.
     */
    public void deleteProject(String projectID, String creator) {
        // First check for null values
        if (projectID == null || projectID.isEmpty())
            throw new IllegalArgumentException("Project ID cannot be empty");
        // Check if the project exists in the database
        if (!projects.containsKey(Integer.parseInt(projectID)))
            throw new IllegalArgumentException("Project does not exist");
        else {
            // Check if the user is the creator of the project
            if (projects.get(Integer.parseInt(projectID)).getCreator().equals(creator.toLowerCase())) {
                projects.remove(Integer.parseInt(projectID));
            } else {
                throw new IllegalArgumentException("You are not the creator of this project");
            }
        }

    }

    /**
     * This method is used for opening a project to view the storyBoard.
     *
     * @param projectID - The project ID of the project.
     * @param creator   - The creator of the project.
     * @return - Returns a map of the project.
     */
    public Map<Integer, Project> openProject(String projectID, String creator) {
        Map<Integer, Project> project = new HashMap<>();
        // First check for null values
        if (projectID == null || projectID.isEmpty())
            throw new IllegalArgumentException("Project ID cannot be empty");
        // Check if the project exists in the database
        if (!projects.containsKey(Integer.parseInt(projectID)))
            throw new IllegalArgumentException("Project does not exist");
        else {
            // Check if the user is the creator of the project or a team member
            if (projects.get(Integer.parseInt(projectID)).getCreator().equals(creator.toLowerCase()) || projects.get(Integer.parseInt(projectID)).getTeamMembers().contains(creator.toLowerCase())) {
                project.put(Integer.parseInt(projectID), projects.get(Integer.parseInt(projectID)));

            } else {
                throw new IllegalArgumentException("You do not have access to this project");
            }
        }
        return project;
    }
}

