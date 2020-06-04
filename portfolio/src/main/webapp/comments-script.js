
//Retrieve data from the server and create a comment Element for each response
function callFetch(){
    fetch("/data")
    .then(res => res.json())
    .then(res => {
        if(res.length === 0){
            next();
        }
        res.forEach(comment => {
            createCommentElement(comment);
        })
    })
    .catch(err => {
        console.err(err);
        renderEmpty();
    });
}

/**
This function creates a div from the comment object passed in
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

    //Create comment content
    const commentTag = document.createElement("p");
    const text = document.createTextNode(comment.comment);
    commentTag.appendChild(text);

    //Create bottom content
    const bottom_div = document.createElement("div");
    bottom_div.classList.add("lowest-div");

    //Create container for the person
    const username_tag = document.createElement("p");
    username_tag.classList.add("lowest-tag");
    const usercontent = document.createTextNode(comment.userName);
    username_tag.appendChild(usercontent);

    //Create container for time tag
    const timeFormatted = getDateTimeFromTimestamp(comment.timestamp);
    const time_tag = document.createElement("p");
    time_tag.classList.add("lowest-tag");
    const time = document.createTextNode(timeFormatted);
    time_tag.appendChild(time);

    bottom_div.appendChild(username_tag);
    bottom_div.appendChild(time_tag);

    commentContainer.appendChild(commentTag);
    commentContainer.appendChild(bottom_div);
    
    main.appendChild(commentContainer);
}

//This function renders an svg to the html to show no data
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

//This function converts the unix timestamp to date and time format
function getDateTimeFromTimestamp(unixTimeStamp) {
    var date = new Date(unixTimeStamp);
    return ('0' + date.getDate()).slice(-2) + '/' + ('0' + (date.getMonth() + 1)).slice(-2) + '/' + date.getFullYear() + ' ' + ('0' + date.getHours()).slice(-2) + ':' + ('0' + date.getMinutes()).slice(-2);
}

//Add an event listener to the submit button that send the response as JSON to the server
//Later, this will allow us to add data from cookies

document.querySelector('form').addEventListener('submit', (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    data = {
        comment: formData.get("comment"),
        userName: "Mohamed Shatry",
        timestamp: 0
    }

    fetch("/data", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    })
    .then(res => res.json())
    .then(res => {
        console.log("Got response");
        console.log(res);
    })
    .catch(err => console.error(err));

    event.currentTarget.submit();
});


