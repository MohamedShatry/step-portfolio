
function callFetch(){
    fetch("/data")
    .then(res => res.json())
    .then(res => {
        console.log(res);
        res.forEach(comment => {
            createCommentElement(comment);
        })
    })
    .catch(err => console.err(err));
}

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

    //Create container for tag
    const timeFormatted = new Date(comment.timestamp * 1000).toLocaleDateString("en-US");

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

document.querySelector('form').addEventListener('submit', (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    const comment = formData.get("comment");
    data = {
        comment: formData.get("comment"),
        userName: "Mohamed Shatry"
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
    console.log(formData);
    console.log(comment);

    e.run();
});

