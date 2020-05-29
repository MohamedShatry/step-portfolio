// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/*
	*Dynamically fetch content from github and render it to html

    *Description of functions below:
        *getData() asyncronously fetched data from github and calls functions to parse, filter and render component\
        *injectHeader(header) dynamically renders the header to the body
        *injectRepo(repo) creates a new repository elements and injects it into the body
        *injectError() injects an error svg into the html file

*/

async function getData (){
    await fetch('https://api.github.com/users/MohamedShatry/repos')
            .then(data => data.json())
            .then(res => {
                if(res.length === 0){
                    next();
                }
                let repo = res[0].owner;
                let varHeader = {
                    title : repo.login,
                    url: repo.html_url,
                    avatar_url: repo.avatar_url
                }
                injectHeader(varHeader);
                
                res.forEach(repo => {
                    var returnRepo = {
                        title: repo.name,
                        url: repo.html_url,
                        language: repo.language,
                        description: repo.description,
                        web: repo.homepage
                    }
                    injectRepo(returnRepo);
                });
            })
            .catch(err => {
                console.log("Error", err);
                injectError();
            }); 
}

/*
	*Gets data from {header} object and creates elements to render to html
    *Resulting html looks like this: 

    	<div id="git-header">
        	<a href={{ header.url }} target="_blank">
            	<img class="avatar" src= {{ header.avatar_url }}>
            </a>
            <b class="git-header-title"> {{ header.title }} </b>
        </div>

*/
function injectHeader(header){
    const gitHeader = document.getElementById("git-header");
	
    const avatar_link = document.createElement("a");
    avatar_link.href = header.url;
    avatar_link.target = "_blank";

    const avatar = document.createElement("img");
    avatar.classList.add("avatar");
    avatar.src = header.avatar_url;
    
	avatar_link.appendChild(avatar);

    const title_tag = document.createElement("span");
    title_tag.classList.add("git-header-title");
    title_tag.classList.add("bold");
    const title = document.createTextNode(header.title);
    title_tag.appendChild(title);
    
    gitHeader.appendChild(avatar_link);
    gitHeader.appendChild(title_tag);

}

/*
	*Gets data from {repo} object and creates elements to render to html
    *Resulting html looks like this: 
	
        <div class="repo-content">
        	<p class="repo-header">{{ repo.title }}</p>
            <p> {{ repo.description }} </p>
            <div class="lowest-div">
            	<p class="lowest-tag"> {{repo.language}} </p>
                <a href= {{ repo.url }} target="_blank">
                	<p class="lowest-tag arrow-link">View Code→</p>
                </a>

                <!-- Only rendered if repo has a web url-->
                <a href= {{ repo.web }} target="_blank">
                	<p class="lowest-tag arrow-link">View Live→</p>
                </a>

            </div>
        </div>

*/
function injectRepo(repo){
    const main = document.getElementById("repo-container");

	//Create container to hold content parsed in
    const repoContainer = document.createElement("div");
    repoContainer.classList.add("repo-content");
	
    //Create Container heading
    const title_tag = document.createElement("p");
    title_tag.classList.add("repo-header");
    const title = document.createTextNode(repo.title);
    title_tag.appendChild(title);
	
    //Create container description
    const description_tag = document.createElement("p");
    const description = document.createTextNode(repo.description);
    description_tag.appendChild(description);
	
    //Create div that will hold the language of each repo and the associated links 
    const bottom_div = document.createElement("div");
    bottom_div.classList.add("lowest-div");
	
	//Create containet for the language
    const language_tag = document.createElement("p");
    language_tag.classList.add("lowest-tag");
    const language = document.createTextNode(repo.language);
    language_tag.appendChild(language);
	
    //Create container that will link to the Github
    const url_link = document.createElement("a");
    url_link.href = repo.url;
    url_link.target = "_blank";

    const url_tag = document.createElement("p");
    url_tag.classList.add("lowest-tag");
    url_tag.classList.add("arrow-link");
    
    const url_text = document.createTextNode("View Code→");
    url_tag.appendChild(url_text);
    url_link.appendChild(url_tag);


    //Append everything to the bottom div
    bottom_div.appendChild(language_tag);
    bottom_div.appendChild(url_link);
    
    //Conditionally create container if repo has a web url
	if(repo.web !== null){
        const web_link = document.createElement("a");
        web_link.href = repo.web;
        web_link.target = "_blank";

        const web_tag = document.createElement("p");
        web_tag.classList.add("lowest-tag");
        web_tag.classList.add("arrow-link");

        const web_text = document.createTextNode("View Live→");
        web_tag.appendChild(web_text);
        web_link.appendChild(web_tag);
        bottom_div.appendChild(web_link);
    }

	//Append everything to the repo container
    repoContainer.appendChild(title_tag);
    repoContainer.appendChild(description_tag);
    repoContainer.appendChild(bottom_div);

    main.appendChild(repoContainer);

}

//Inject no data page if no data was returned;
function injectError(){
    const main = document.getElementById("repo-container");

    const error = document.createElement("img");
    error.classList.add("error");
    error.src = "images/no_data.svg";

    const error_tag = document.createElement("p");
    error_tag.classList.add("error-tag");
    const err_text = document.createTextNode("Ooops! Looks like we found nothing!");
    error_tag.appendChild(err_text);

    main.appendChild(error);
    main.appendChild(error_tag);

}

//Fire function
getData();