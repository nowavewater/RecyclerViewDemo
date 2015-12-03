package com.example.recyclerviewdemo;


import java.util.ArrayList;
import java.util.List;

public class StudentListHelper {

    // A "database" helper class, which create a list of students
    // This could be different depend on your application

    private int total = 50;

    private List<Student> studentList;
    private int maxSize;

    public StudentListHelper() {
        refresh();
    }

    public void refresh() {

        // Clear the student list, recreate a new list

        if (studentList != null && studentList.size() > 0)
            studentList.clear();
        studentList = new ArrayList<Student>();
        for(int i=0; i<50; i++){
            Student student = new Student();
            student.setId(i+1);
            studentList.add(student);
        }
        maxSize = studentList.size();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public List<Student> addToList(List<Student> list) {

        // Add the number of RECORD_COUNT student to the list upon request

        int start = list.size();
        int end = start + Constants.RECORD_COUNT;
        while ( start<end && start < maxSize  ) {
            list.add(studentList.get(start++));
        }
        return list;
    }
}
