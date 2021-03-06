var credentials = {};

function init(){
    fetch("/auth")
    .then(res => res.json())
    .then(res => {
        credentials = res;
        document.getElementById("loginRoute").innerHTML = res.loggedIn ? "Logout" : "Login";
        document.getElementById("loginRoute").setAttribute("href", res.loggedIn ? res.logoutUrl : res.loginUrl);
        loadComments();
    })
    .catch(err => console.error(err));
}

//Retrieve data from the server and create a comment Element for each response.
function loadComments(){
    document.getElementById("commentForm").style.visibility = credentials.loggedIn ? "visible" : "hidden";
    const reqNum = document.getElementById("quantity").value;
    const reqLanguage = document.getElementById("language").value;

    let url = "/data?num="+reqNum+"&lang="+reqLanguage;
    fetch(url)
    .then(res => res.json())
    .then(res => {
        if(res.length === 0){
            next();
        }
        document.getElementById("comment-content").innerHTML = "";
        res.forEach(comment => {
            createCommentElement(comment);
        })
    })
    .catch(err => {
        console.error(err);
        renderEmpty();
    });
}

/**
This function creates a div from the comment object passed in.
Resulting div will look like this:
    <div class="comment-bar">
        <p> {{ comment.comment }} </p>
        <div class="lowest-div">
            <p class="lowest-tag">{{ comment.userName }}</p>
            <p class="lowest-tag"> {{ comment.timestamp | Date }} </p>
        </div>
    </div>

 */
function createCommentElement(comment){
    const main = document.getElementById("comment-content");

    const commentContainer = document.createElement("div");
    commentContainer.classList.add("comment-bar");

    //Create comment content.
    const commentTag = document.createElement("p");
    const text = document.createTextNode(comment.comment);
    commentTag.appendChild(text);

    //Create bottom content.
    const bottom_div = document.createElement("div");
    bottom_div.classList.add("lowest-div");

    //Create container for the person.
    const username_tag = document.createElement("p");
    username_tag.classList.add("lowest-tag");
    const usercontent = document.createTextNode(comment.email);
    username_tag.appendChild(usercontent);

    //Create container for time tag.
    const timeFormatted = new Date(comment.timestamp).toString().substring(0,21);
    const time_tag = document.createElement("p");
    time_tag.classList.add("lowest-tag");
    const time = document.createTextNode(timeFormatted);
    time_tag.appendChild(time);
    
    bottom_div.appendChild(username_tag);
    bottom_div.appendChild(time_tag);

    //Create delete button.
    if(credentials.email === comment.email){
        const deleteBtn = document.createElement("BUTTON");
        deleteBtn.onclick = deleteComment;
        deleteBtn.setAttribute("id", comment.id.toString());
        deleteBtn.setAttribute("class", "btn btn-blue");
        deleteBtn.innerHTML = "Delete";
        bottom_div.appendChild(deleteBtn);
    }

    commentContainer.appendChild(commentTag);
    commentContainer.appendChild(bottom_div);
    
    main.appendChild(commentContainer);
}

//This function renders an svg to the html to show no data.
function renderEmpty(){
    const main = document.getElementById("comment-content");

    const error = document.createElement("img");
    error.classList.add("error");
    error.src = "images/no_data.svg";

    const error_tag = document.createElement("p");
    error_tag.classList.add("error-tag");
    const err_text = document.createTextNode("Ooops! Looks like there are no comments to show! Type one");
    error_tag.appendChild(err_text);

    main.appendChild(error);
    main.appendChild(error_tag);
}


//Add an event listener to the submit button that send the response as JSON to the server.
function submitForm() {
    const formData = new FormData(document.getElementById("commentForm"));

    if(formData.get("comment") === ''){
        alert("Text area cannot be empty");
        return;
    }

    data = {
        comment: formData.get("comment"),
        email: credentials.email,
        timestamp: 0,
        id: 0
    }

    fetch("/data", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    })
    .catch(err => console.error(err));

    event.currentTarget.submit();
}

//This function allows users to delete the comments.
function deleteComment() {
    const reqID = this.id;
    const url = "/data?id="+reqID;
    fetch(url, {
        method: 'DELETE',
    })
    .catch(err => console.error(err));
    location.reload(true);
}
