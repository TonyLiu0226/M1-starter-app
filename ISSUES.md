# M1

## List of issues

### Issue 1: [Error handling in sign in: If an error occurs, the user is simply redirected back to the sign in/sign up screen with no indication of what went wrong]

**Description**:[WRITE_ISSUE_DESCRIPTION]

**How it was fixed?**: [WRITE_ISSUE_SOLUTION]

### Issue 2: Backend files were initially all dumped inside one large src folder with no organization. This is bad code structure, and should be organized into folders for functionality (eg: all controllers in one folder, all services in a seperate one)

**Description**:[WRITE_ISSUE_DESCRIPTION]

**How it was fixed?**: Refactoring of files into the distinct folders mentioned above. Used AI Agent to fix import issues resulting from the reorganization of files.

### Issue 3: When the user clicks on save in manage profile screen, the profile picture reverts back to the defualt google account's profile photo, regardless of what it was before

**Description**: The AI agent identified the bug, and it turns out that in the ProfileViewModel.kt the frontend is simply updating the profile photo of the user locally within the app (it only affects the uistate.value.user). This means, if you log out then log back in, the image will also likely revert.

**How it was fixed?**: The initial implementation has already implemented an API call to media/upload in ImageInterface interface in the api/ProfileInterface.kt file, but it was never actually called. So the fix was to ensure that ProfileRepositoryImpl.kt file actually calls the interface we defined. This was done by making a new function called uploadProfilePicture. ProfileViewModel.kt's uploadProfilePicture function was then modified so that instead of simply modifying the photo locally within the UI, it now calls the newly created uploadProfilePicture function in ProfileRepositoryImpl.
...
