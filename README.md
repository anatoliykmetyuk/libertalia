Libertalia is a tool to assist in planning your tasks using the Country model.

# Installation
1. Clone the repository
2. Create MariaDB database named "libertalia" at localhost
3. Configure the text editor you would like to use for editing files in the `~/.libertalia` file. So far, the only legit entry there is `editor=<editor_command>`. `<editor_command>` should be such that:
    - It accepts one argument for opening the file: `<editor_command> <file_name>`
    - It is blocking, that is after entering it, the command prompt will not appear until you close the editor.
4. Run it with `sbt run`

# Usage
After running `sbt run`, a prompt will appear. The commands you can enter have the following structure: `<module_name> <module_command>`. So far, the legit module names are the following:

- `org` - Organizations
- `doc` - Documents
- `msg` - Messages

Also you can enter `exit` to exit.

`<module_command>` tells the module what to do. Most of the modules employ commands defined in `libertalia/Cmd.scala`.

More precise specification of what commands are available for each module are available in the sources of these modules under `libertalia/module/`.

# Example
```
Libertalia v0.0.1 console
libertalia> org

libertalia> org mk President
Created: 1 President
libertalia> org
1 President
libertalia> org mk "Departmnet of Economy"
Created: 2 Departmnet of Economy
libertalia> org
1 President
2 Departmnet of Economy
libertalia> org mv 2 1
Updated: 2 Departmnet of Economy
libertalia> org
1 President
    2 Departmnet of Economy
libertalia> org mk "Academy of Sciences" 1
Created: 3 Academy of Sciences
libertalia> org
1 President
    2 Departmnet of Economy
    3 Academy of Sciences
libertalia> msg mk 1 2 "Let's make some money!"
Created: 1 msg
From: 1
To  : 2
Excerpt:
adsadsasd
libertalia> msg of 2
1 msg
From: 1
To  : 2
Excerpt:
adsadsasd
libertalia>
```