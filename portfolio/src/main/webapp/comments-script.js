
function callFetch(){
    fetch("/data")
    .then(res => res.json())
    .then(res => console.log(res))
    .catch(err => console.err(err));
}