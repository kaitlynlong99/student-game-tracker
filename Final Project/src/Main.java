import java.util.*;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        // HashMap to store student names and corresponding Student objects
        Map<String, Student> studentNames = new HashMap<>();
        BinarySearchTree gradeTree = new BinarySearchTree();

        int choice;

        while (true) {
            try {
                // Display menu options
                System.out.println("\nStudent Grade Tracker");
                System.out.println("1. Add Student");
                System.out.println("2. Remove Student");
                System.out.println("3. Add Assignment Grade");
                System.out.println("4. Update Assignment Grade");
                System.out.println("5. View Student Grades");
                System.out.println("6. View Top Students");
                System.out.println("7. View Sorted Grades");
                System.out.println("8. Exit");

                System.out.print("Enter your choice: ");
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume the newLine character
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine(); // Clear invalid input
                    promptToContinue(scanner);
                    continue;
                }

                if (choice == 8) {
                    break; // Exit the program
                }

                boolean continueAction = true;
                while (continueAction) {
                    switch (choice) {
                        case 1:
                            addStudent(scanner, studentNames);
                            break;
                        case 2:
                            removeStudent(scanner, studentNames, gradeTree);
                            break;
                        case 3:
                            assignGrades(scanner, studentNames, gradeTree);
                            break;
                        case 4:
                            updateGrades(scanner, studentNames, gradeTree);
                            break;
                        case 5:
                            viewGrades(scanner, studentNames);
                            break;
                        case 6:
                            topStudents(scanner, studentNames);
                            break;
                        case 7:
                            viewSortedGrades(gradeTree);
                            break;
                        default:
                            System.out.println("Invalid choice. Please select a valid option. ");
                            promptToContinue(scanner);
                            continueAction = false;
                            break;
                    }

                    // Ask user if they want to perform the same action again
                    if (continueAction) {
                        System.out.print("\nWould you like to perform the same action again? (yes/no): ");
                        String answer = scanner.nextLine().trim().toLowerCase();

                        if (!answer.equals("yes")) {
                            continueAction = false;
                        }
                    } else {
                        break;
                    }
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
                scanner.nextLine(); // Clear invalid input
                promptToContinue(scanner);
            }
        }

        System.out.println("You have exited the program.");
        scanner.close();
    }

    // Helper method to prompt user to continue after an error
    public static void promptToContinue(Scanner scanner) {
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    // Adds a new student to the system
    public static void addStudent(Scanner scanner, Map<String, Student> studentNames) {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();

        if (!studentNames.containsKey(name)) {
            studentNames.put(name, new Student(name));
            System.out.println("Student " + name + " added.");
        } else {
            System.out.println("Student " + name + " already exists.");
        }
    }

    // Removes a student from the system
    public static void removeStudent(Scanner scanner, Map<String, Student> studentNames, BinarySearchTree gradeTree) {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine().trim();

        if (studentNames.containsKey(name)) {
            Student student = studentNames.get(name);
            double oldAverage = student.getAverageGrade();

            studentNames.remove(name);
            System.out.println("Student " + name + " removed.");

            gradeTree.delete(oldAverage, name);
        } else {
            System.out.println("Cannot remove student. Student not found.");
        }
    }

    // Assigns grades to a student
    public static void assignGrades(Scanner scanner, Map<String, Student> studentNames, BinarySearchTree gradeTree) {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine().trim();

        // Check if the student exists in the system
        if (!studentNames.containsKey(name)) {
            System.out.println("Student not found.");
            return;
        }

        Student student = studentNames.get(name);
        boolean addMoreGrades = true;

        // Validate that input is a number within the grade range
        while (addMoreGrades) {
            System.out.print("Enter grade (0-100): ");
            if (scanner.hasNextInt()) {
                int grade = scanner.nextInt();
                scanner.nextLine(); // Consume the newLine

                if (grade >= 0 && grade <= 100) {
                    double oldAverage = student.getAverageGrade();
                    student.addGrade(grade);
                    gradeTree.delete(oldAverage, name);
                    gradeTree.insert(student.getAverageGrade(), name);
                    System.out.println("\nGrade added for " + name + ".");
                } else {
                    System.out.println("\nInvalid grade. Please enter a value between 0 and 100. ");
                }
            } else {
                System.out.println("\nInvalid input. Please enter a number.");
                scanner.next();
            }

            // Ask if user wants to add another grade
            System.out.print("Would you like to add another grade for " + name + "? (yes/no): ");
            addMoreGrades = scanner.nextLine().trim().equalsIgnoreCase("yes");
        }
    }

    // Updates an existing grades for a student
    public static void updateGrades(Scanner scanner, Map<String, Student> studentNames, BinarySearchTree gradeTree) {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine().trim();

        // Check if student exists
        if (!studentNames.containsKey(name)) {
            System.out.println("Student not found.");
            return;
        }

        Student student = studentNames.get(name);
        student.viewGradesWithoutAverage(); // Display current grades before update

        boolean validIndex = false;
        int gradeIndex = -1;

        // Loop to ensure user enters a valid index
        while (!validIndex) {
            System.out.print("\nEnter index of grade to update: ");
            if (scanner.hasNextInt()) {
                gradeIndex = scanner.nextInt();
                scanner.nextLine(); // Consume newLine
                if (gradeIndex >= 0 && gradeIndex < student.grades.size()) {
                    validIndex = true; // Valid index found
                } else {
                    System.out.println("Invalid index. Please enter a valid grade index.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear invalid input
            }
        }

        // Prompt for new grade
        System.out.print("Enter new grade: ");
        if (scanner.hasNextInt()) {
            int newGrade = scanner.nextInt();
            scanner.nextLine(); // Consume newLine

            // Validate new grade
            if (newGrade >= 0 && newGrade <= 100) {
                double oldAverage = student.getAverageGrade();
                student.updateGrade(gradeIndex, newGrade);
                double newAverage = student.getAverageGrade();

                gradeTree.delete(oldAverage, name);
                gradeTree.insert(newAverage, name);
                System.out.println("Grade updated for " + name + ".");
            } else {
                System.out.println("Invalid grade. Please enter a value between 0 and 100.");
            }
        } else {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // Clear invalid input
        }
    }

    // Displays a student's grades
    public static void viewGrades(Scanner scanner, Map<String, Student> studentNames) {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine().trim();

        if (studentNames.containsKey(name)) {
            studentNames.get(name).viewGrades();
        } else {
            System.out.println("Student not found.");
        }
    }

    // Displays students' grades in sorted order
    public static void viewSortedGrades(BinarySearchTree gradeTree) {
        System.out.println("\nDisplaying students' grades in sorted order: ");
        gradeTree.inorder();
    }

    // Displays top students based on average grade
    public static void topStudents(Scanner scanner, Map<String, Student> studentNames) {
        // Check if there are any students in the system
        if (studentNames.isEmpty()) {
            System.out.println("No students available.");
            return;
        }

        // Create a list to store student names along with their average grades
        List<Map.Entry<String, Double>> topStudentsList = new ArrayList<>();

        // Iterate over the student map and calculate each student's average grade
        for (Map.Entry<String, Student> entry : studentNames.entrySet()) {
            // Store student name and their average grade as a key-value pair
            topStudentsList.add(Map.entry(entry.getKey(), entry.getValue().getAverageGrade()));
        }

        // Sort the list in descending order based on average grade
        topStudentsList.sort((entry1, entry2) -> entry2.getValue().
                compareTo(entry1.getValue()));

        int numberStudents = -1; // Variable to store user input for the number of top students

        // Loop to ensure a valid positive number is entered
        while (numberStudents <= 0) {
            System.out.print("\nHow many top students to display? ");

            if (scanner.hasNextInt()) {
                numberStudents = scanner.nextInt(); // Read user input
                scanner.nextLine(); // Consumer newLine

                // Ensure the input is a positive number
                if (numberStudents <= 0) {
                    System.out.println("Please enter a positive number.");
                }
            } else {
                System.out.println("Invalid input. Please enter a positive number.");
                scanner.nextLine(); // Consume invalid input
            }
        }

        // Display the top 'numberStudents' students
        System.out.println("Top " + numberStudents + " student(s): ");
        for (int i = 0; i < numberStudents && i < topStudentsList.size(); i++) {
            Map.Entry<String, Double> entry = topStudentsList.get(i);
            // Print student ranking, name, and formatted average grade
            System.out.printf("%d. %s with average: %.2f\n", i + 1,  entry.getKey(), entry.getValue());
        }
    }

    // Student class to store grades and calculate averages
    public static class Student {
        private String name;
        private ArrayList<Integer> grades;
        private double averageGrade;

        public Student(String name) {
            this.name = name;
            this.grades = new ArrayList<>();
            this.averageGrade = 0.0;
        }

        // Calculates the average grade of the student
        public double calculateAverage() {
            if (grades.isEmpty()) {
                return 0; // Return 0 if no grades are available to avoid division by zero
            }
            double sum = 0;

            // Sum up all grades
            for (Integer grade : grades) {
                sum += grade;
            }

            // Return the calculated average
            return sum / grades.size();
        }

        // Adds a grade to the student's grades list and updates the average grade
        public void addGrade(int grade) {
            if (grade >= 0 && grade <= 100) {
                grades.add(grade);
                averageGrade = calculateAverage();
            } else {
                System.out.println("Grade is not within valid range. Please enter a valid grade.");
            }
        }

        // Updates a specific grade in the student's grades list by index
        public void updateGrade(int gradeIndex, int newGrade) {
            if (gradeIndex >= 0 && gradeIndex < grades.size()) {
                grades.set(gradeIndex, newGrade);
                averageGrade = calculateAverage();

            } else {
                System.out.println("Index out of bounds. Please enter a valid index.");
            }
        }

        // Returns the average grade of the student
        public double getAverageGrade() {
            return this.averageGrade;
        }

        // Views the student's grades along with their average grade
        public void viewGrades() {
            if (grades.isEmpty()) {
                System.out.println("No grades available for this student.");
            } else {
                System.out.println(name + "'s Grades: " + grades);
                System.out.printf("Average grade: %.2f%n", getAverageGrade());
            }
        }

        // Views the student's grades without displaying the average grade
        public void viewGradesWithoutAverage() {
            System.out.println(name + "'s Grades: " + grades);
        }
    }

    // BinarySearchTree class to manage and sort student grades in a binary search tree structure
    public static class BinarySearchTree {

        // Node class to represent each grade and associated student names in the tree
        private class Node {
            double grade;
            List<String> names;
            Node left, right;

            // Constructor to create a node with a grade and associated names
            public Node (double grade, List<String> names) {
                this.grade = grade;
                this.names = names;
                this.left = this.right = null;
            }
        }

        Node root; // Root node of the tree

        // Constructor for the BinarySearchTree class
        public BinarySearchTree() {
            root = null; // initialize the tree as empty
        }

        // Inserts a new grade and student name into the tree
        public void insert(double grade, String name) {
            root = insertHelper(root, grade, name);
        }

        // Deletes a student and their grade from the tree
        public void delete(double grade, String name) {
            root = deleteHelper(root, grade, name);
        }

        // Traverses the tree in-order and prints the grade and associated names
        public void inorder() {
            inorderHelper(root);
        }

        // Helper function to insert a new node into the tree
        private Node insertHelper(Node current, double grade, String name) {
            if (current == null) {
                List<String> names = new ArrayList<>();
                names.add(name);
                return new Node(grade, names);
            }
            if (grade == current.grade) {
                if (current.names.stream().noneMatch(n -> n.equalsIgnoreCase(name))) {
                    current.names.add(name);
                }
            } else if (grade < current.grade) {
                current.left = insertHelper(current.left, grade, name);
            } else if (grade > current.grade) {
                current.right = insertHelper(current.right, grade, name);
            }
            return current;
        }

        // Helper function to delete a node from the tree
        private Node deleteHelper(Node current, double grade, String name) {
            if (current == null) {
                return null;
            }
            if (grade == current.grade) {
                current.names.remove(name);

                // If no more students have this grade, remove the node
                if (current.names.isEmpty()) {
                    if (current.left == null && current.right == null) {
                        return null;
                    }
                    if (current.left == null) {
                        return current.right;
                    }
                    if (current.right == null) {
                        return current.left;
                    }

                    Node minNode = findMin(current.right);
                    current.grade = minNode.grade;
                    current.names = new ArrayList<>(minNode.names);
                    current.right = deleteHelper(current.right, minNode.grade, minNode.names.get(0));
                }
            }
            else if (grade < current.grade) {
                current.left = deleteHelper(current.left, grade, name);
            }
            else {
                current.right = deleteHelper(current.right, grade, name);
            }
            return current;
        }

        // Finds the node with the minimum grade in the tree
        private Node findMin(Node current) {
            while (current.left != null) {
                current = current.left;
            }
            return current;
        }

        // Helper function to traverse the tree in-order and print grade and names
        private void inorderHelper(Node current) {
            if (current != null) {
                inorderHelper(current.left);
                List<String> sortedNames = new ArrayList<>(current.names);
                sortedNames.sort(String.CASE_INSENSITIVE_ORDER); // Sort names alphabetically
                System.out.println("Grade: " + current.grade + " | Name(s): " + String.join(", ", sortedNames));
                inorderHelper(current.right);
            }
        }
    }
}

