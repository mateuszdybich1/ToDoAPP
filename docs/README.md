<a name="readme-top"></a>
<!--
*** Thanks for checking out the ToDoAPP. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->







<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/mateuszdybich1/ToDoAPP">
    <img src="img/logo.png" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">ToDo APP</h3>

  <p align="center">
    Simple app for groups to organize tasks in project.
    <br />
    <a href="https://github.com/mateuszdybich1/ToDoAPP"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/mateuszdybich1/ToDoAPP">View Demo</a>
    ·
    <a href="https://github.com/mateuszdybich1/ToDoAPP/issues">Report Bug</a>
    ·
    <a href="https://github.com/mateuszdybich1/ToDoAPP/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

This project is created for small groups where there is a team leader (manager) and other employees to help them create, manage and  organize tasks. Only team leader can add employee to project, remove employee from project, add tasks and delete them. Regular employee can only start and finish tasks. 


How to use this app:
* After a few seconds of splash screen, user will be taken to the login view. Next, he has to click on the purple text "Sign up now", and will be redirected to the registration view.
<style>
.grid-container {
  display: grid;
    
  grid-template-columns: auto auto;
   align-content: center;
    padding-bottom: 20px;
    alignment: center;
    grid-gap: 40px;
    justify-content: center;
}
.grid-container2 {
  display: grid;
    
  grid-template-columns: auto;
    place-items: center;

    padding-bottom: 50px;
    padding-left: 50px;
    padding-right: 50px;
    grid-gap: 40px;
    
    
    
}

</style>
<div class="grid-container">
    
  <img src="img/imgSplashScreen.png" height="400" alt="Splash Screen">
  <img src="img/imgLogin.png" height="400" alt="Login Activity">

  
</div>


* User must provide all data, i.e. username, email, password and its repetition. Then decide whether he is the project leader, and click switch button. If all data is unique, then he will be transferred to the view with a 4-digit pin code that
  came to the e-mail address. After entering the code correctly, user will be redirected to
  main view with tasks.
<div class="grid-container">
  <img src="img/imgRegister.png" height="400" alt="Register Activity">
  <img src="img/imgConfirmMail.png" height="400" alt="Confirm Mail Activity">
<img src="img/imgConfirmMail_code.png" height="400" alt="Email code">
<img src="img/imgToDo.png" height="400" alt="ToDo Fragment">
</div>

* After logging in, the user sees 4 sections: "ToDo", "Doing", "Done",
and "Profile". In the "ToDo", "Doing", and "Done" sections, the user sees
task tiles, and can click on a particular task to
view its details, and hold to perform the operation.
<div class="grid-container">
  <img src="img/imgTaskInfo.png" height="400" alt="Add To Project bottom sheet">
</div>

- "ToDo" - If the user is a leader, he sees a button
  which, when pressed, sees a list with 2 buttons ("Add employee" and "Add task"):
<div class="grid-container"><img src="img/imgToDo_btnClicked.png" height="400" alt="ToDo Fragment button clicked"></div>

"Add employee" - when leader clicks on this button, he can add employee to project by entering email of employee and clicking button "Add to project".
<div class="grid-container">
  <img src="img/imgAddToProject.png" height="400" alt="Add To Project bottom sheet">
</div>


"Add task" - when clicked, user is redirected to the view with fields to fill to add new task for the project. Base deadline date is set to the end of next day.
<div class="grid-container">
  <img src="img/imgAddTask.png" height="400" alt="Add Task activity">

</div>

<div class="grid-container">
<img src="img/imgAddTask_deadlineDate.png" height="400" alt="Deadline Date Fragment">
<img  src="img/imgAddTask_deadlineHour.png" height="400" alt="Deadline Hour Fragment">
</div>

In this section user can start task by holding on task tile and then clicking "Start doing" button. Nickname of user who started task will be assigned to the "Started by" field in task description. Task will be moved to "Doing" section. If user is a manager, he can also delete task by clicking "Delete" button. Regular employee doesn't see this button.

<div class="grid-container">
  <img src="img/imgToDo_bottomSheet.png" height="400" alt="ToDo bottom sheet">

</div>

* "Doing" - In this section user can see a list of tasks that are in progress. To finish task, user has to hold on task tile and then click "Finish" button. Task will be moved to "Done" section. Nickname of user who finished task will be assigned to the "Finished by" field in task description. If user is a manager, he can also delete task by clicking "Delete" button. Regular employee doesn't see this button.
<div class="grid-container">

  <img src="img/imgDoing.png" height="400" alt="Doing Fragment">
    <img src="img/imgDoing_bottomSheet.png" height="400" alt="Doing bottom sheet">
</div>

- "Done" - In this section you can see a list of all tasks that have been completed
  have been completed. If user is a manager, he can also delete task by clicking "Delete" button after holding on task tile. Regular employee doesn't see this button.
<div class="grid-container">

  <img src="img/imgDone.png" height="400" alt="Done Framgent">
    <img src="img/imgDone_bottomSheet.png" height="400" alt="Done bottom sheet">
</div>

- "Profile" - User can view information about the account: nickname
  username, email address and leader's nickname. Team members list is available after clicking on "Team list" button. In case of,
  when the user is a leader, he has option to remove
  any employee from the team with the "Remove" button. The second button is
  “Undone Tasks” which does nothing right now, because it isn't finished yet. When the deadline of the task from section "ToDo" or "Doing" have exceeded, task will be moved to the section available after clicking on this button. List will contain tasks which weren't started or finished before the deadline of the task. 
<div class="grid-container">
    <img src="img/imgProfile.png" height="400" alt="Profile Fragment">
    <img src="img/imgEmployeeList.png" height="400" alt="Employee List Activity">
</div>

Database structure:

<div class="grid-container2">
    <img src="img/auth.png" alt="auth img1">
    <img src="img/auth2.png"  alt="auth img2">
    <img  src="img/db1.png"  alt="db img1">
<img  src="img/db2.png"  alt="db img2">
<img  src="img/db3.png"  alt="db img3">
<img  src="img/db4.png"  alt="db img4">
<img  src="img/db5.png"  alt="db img5">
<img  src="img/db6.png"  alt="db img6">
<img  src="img/db7.png"  alt="db img7">
</div>

PHP SMTP API:
<div class="grid-container2">
    <img src="img/php.png" alt="php API">
</div>


<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With


* [![Next][IntelliJ]][IntelliJ-url]
* [![Next][Kotlin]][Kotlin-url]
* [![Next][PHP]][PHP-url]
* [![Next][Firebase]][Firebase-url]
* [![Next][Material]][Material-url]
* [![Next][Volley]][Volley-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTACT -->
## Contact

Mateusz Dybich  - mateuszdybich1@gmail.com

Project Link: [https://github.com/mateuszdybich1/ToDoAPP ](https://github.com/mateuszdybich1/ToDoAPP )

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[Volley]: https://img.shields.io/badge/Volley-yellow
[Volley-url]: https://google.github.io/volley/
[PHP]: https://img.shields.io/badge/PHP-777BB4?logo=php&logoColor=fff&style=for-the-badge
[PHP-url]: https://www.php.net/
[Material]: https://img.shields.io/badge/Material%20Design-757575?logo=materialdesign&logoColor=fff&style=for-the-badge
[Material-url]: https://m2.material.io/
[Firebase]: https://img.shields.io/badge/Firebase-red?style=for-the-badge&logo=Firebase&logoColor=white
[Firebase-url]: https://firebase.google.com/
[Kotlin]: https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white
[Kotlin-url]: https://kotlinlang.org/
[IntelliJ]: https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white
[IntelliJ-url]: https://www.jetbrains.com/idea/