Feature: Retrieve Row Values from Dynamic Web Table

  Scenario: Retrieve Row Values for a Valid Id
    Given the user is on the webpage demo.aspnetawesome.com
    When the user enters the Id "<id>"
    Then the program should find the row with the Id
    And the program should display the values of the found row
    But if the Id is blank, the program should display an error message indicating that the Id is blank
    But if the row with the Id could not be found, the program should display an error message indicating the row was not found

    Examples:
    | id    |
    | 2021  |
    | 2029  |
    | 1337  |
    | 533   |
    | 2028  |
    |       | 
