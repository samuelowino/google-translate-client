src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.js"
/* Platform selection logic
 *platforms can either be ios or android which can be toggled with clicks
 */

const platformBtns = document.querySelectorAll('.platform-btn');
const contentLabel = document.getElementById('contentLabel');
const iosExample = document.getElementById('iosExample');
const androidExample = document.getElementById('androidExample');
let currentPlatform = 'ios';
platformBtns.forEach(btn => {
    btn.addEventListener('click', (e) => {
        platformBtns.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');

        currentPlatform = btn.dataset.platform;
        contentLabel.textContent = currentPlatform === 'ios' ? 'iOS Strings' : 'Android XML';


        iosExample.style.display = currentPlatform === 'ios' ? 'block' : 'none';
        androidExample.style.display = currentPlatform === 'ios' ? 'none' : 'block';
    });
});


iosExample.style.display = 'block';
/*
 *event listener to  handle a translation request
 *handles input validation  and platform handling
 */
document.getElementById('translateForm').addEventListener('submit',async (e) =>{
    e.preventDefault();
    const formatError =document.getElementById("formatError");
    formatError.style.display = 'none';
    const workflow=document.getElementById("workflow").value;
    const languages=document.getElementById("languages").value
        .split(',')
        .map(lang => lang.trim())
        .filter(lang => validLanguages.includes(lang));
    const content =document.getElementById("content").value;
    try{
        const messages = currentPlatform === 'ios' ? parseIosStrings(content) : parseAndroidXml(content);
        if(messages.length === 0){
            throw new Error('No valid messages found in input');
        }
        const payload=currentPlatform === 'ios'
            ? {
                workflow : workflow,
                targetOS : "IOS",
                distinctLanguages: languages,
                iosContent:messages
            }
            : {
                workflow : workflow,
                targetOS : "ANDROID",
                distinctLanguages: languages,
                xmlContent: messages
            };
        const apiKey=sessionStorage.getItem('apiKey');
        if(!apiKey){
            showToast('Not authenticated. Please login again.', 'error');
            window.location.href="./login.html";
            return
        }

        const response = await fetch("/translate",{
            method:"POST",
            credentials: "include",
            headers:{
                "Content-Type" :'application/json',
                "X-API-KEY" : `${apiKey}`
            },
            body:JSON.stringify(payload)
        });
        const data = await response.json();
        if (response.ok) {
            showToast('Translation request submitted successfully! it may take some time to be processed', 'success');

            e.target.reset();
        } else {
            throw new Error(data.message || 'Failed to submit translation request');
        }
        const result=await  pollResult(apiKey, data.response);
    }catch (error) {
        console.error('Submission failed:', error);
        formatError.textContent = error.message;
        formatError.style.display = 'block';
        showToast(error.message || 'Failed to submit translation request', 'error');
    }

});
/*
 *method to check if job is complete
 */
async function pollResult(apiKey, jobId){
    const statusResponse = await fetch(`/translate/${jobId}`,{
        method:"GET",
        credentials: "include",
        headers:{
            "Content-Type" :'application/json',
            "X-API-KEY" : `${apiKey}`
        }
    });
    if (!statusResponse.ok) {
        const error = await statusResponse.json();
        throw new Error(error.message || 'Failed to check translation status');
    }
    const result = await statusResponse.json();
    if(result.code === 202){
        await new Promise(resolve => setTimeout(resolve,5000));
        return await pollResult(apiKey, jobId);
    }

    showTranslationPreview(result);
    return result

}

/*
 *utility to show notifications to users
 */

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    const toastMessage = document.getElementById('toastMessage');

    toast.className = type;
    toastMessage.textContent = message;
    toast.style.display = 'block';

    setTimeout(() => {
        toast.style.display = 'none';
    }, 3000);
}
/*
 *method to parse ios strings to correct format
 */
function parseIosStrings(content){
    const lines =content.split('\n');
    const messages=[];
    lines.forEach(line =>{
        line=line.trim();
        if(line && !line.startsWith('//')){
            const match=line.match(/"([^"]+)"\s*=\s*"([^"]+)"\s*;/);
            if(match){
                messages.push({
                    key:match[1],
                    content:match[2]
                });
            }
        }

    });
    return messages
}

/*
 * method to parse android xml strings
 */
function parseAndroidXml(content){
    const messages=[];
    const parser= new DOMParser();
    const xmlDoc=parser.parseFromString(content,'text/xml')
    if (xmlDoc.getElementsByTagName('parsererror').length > 0) {
        throw new Error('Invalid XML format');

    }
    const strings=xmlDoc.getElementsByTagName('string');
    for(let string of strings){
        messages.push({
            name:string.getAttribute('name'),
            content:string.textContent
        });
    }
    return messages;
}

/*
 *utility  to escape xmlString
 */
function escapeXmlString(str) {
    return str
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&apos;')
        .replace(/([^\\])'/g, "$1\\'");
}

/*
 * utility to escape iosString
 */
function escapeIOSString(str) {
    return str
        .replace(/"/g, '\\"')
        .replace(/\n/g, '\\n');
}

/*
 *utiltity to generate  android resources
 *it creates default strings.xml and assumes original keys are in english
 */
function generateAndroidResources(translationData){
    const zip = new JSZip();
    const resFolder = zip.folder("res");
    const defaultStrings = Object.keys(translationData.translations[Object.keys(translationData.translations)[0]]);
    let defaultXml = '<?xml version="1.0" encoding="utf-8"?>\n<resources>\n';
    defaultStrings.forEach(key => {
        defaultXml += `    <string name="${key}">${key}</string>\n`;
    });
    defaultXml += '</resources>';
    resFolder.file("values/strings.xml", defaultXml);
    Object.entries(translationData.translations).forEach(([langCode, translations]) => {
        let xmlContent = '<?xml version="1.0" encoding="utf-8"?>\n<resources>\n';
        Object.entries(translations).forEach(([key, value]) => {
            const escapedValue = escapeXmlString(value);
            xmlContent += `    <string name="${key}">${escapedValue}</string>\n`;
        });
        xmlContent += '</resources>';
        resFolder.file(`values-${langCode}/strings.xml`, xmlContent);
    });
    zip.generateAsync({type: "blob"})
        .then(content => {
            const url = window.URL.createObjectURL(content);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'android_resources.zip';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        });

}


/*
 *utiltity to generate  ios resources
 *it creates default Base and assumes original keys are in english
 */

function generateiOSResources(translationData){
    const zip = new JSZip();

    const defaultStrings = Object.keys(translationData.translations[Object.keys(translationData.translations)[0]]);
    let baseContent = '/* Base localization */\n\n';
    defaultStrings.forEach(key => {
        baseContent += `"${key}" = "${key}";\n`;
    });
    zip.file("Base.lproj/Localizable.strings", baseContent);


    Object.entries(translationData.translations).forEach(([langCode, translations]) => {
        let content = `/* ${langCode} localization */\n\n`;
        Object.entries(translations).forEach(([key, value]) => {
            const escapedValue = escapeIOSString(value);
            content += `"${key}" = "${escapedValue}";\n`;
        });
        zip.file(`${langCode}.lproj/Localizable.strings`, content);
    });
    zip.generateAsync({type: "blob"})
        .then(content => {
            const url = window.URL.createObjectURL(content);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'ios_resources.zip';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        });

}

/*
 *utility function to show users translation preview for the localized strings
 */
function showTranslationPreview(result) {
    const container = document.getElementById('previewContainer');
    container.style.display = 'block';
    container.innerHTML = '';

    const tabsDiv = document.createElement('div');
    tabsDiv.className = 'preview-tabs';

    const contentDiv = document.createElement('div');
    contentDiv.className = 'preview-content';

    Object.entries(result.translations).forEach(([lang, translations], index) => {
        const tab = document.createElement('button');
        tab.className = `preview-tab ${index === 0 ? 'active' : ''}`;
        tab.textContent = lang;
        tab.onclick = () => switchTab(lang);
        tabsDiv.appendChild(tab);
        const content = document.createElement('div');
        content.className = `translation-item ${index === 0 ? 'active' : ''}`;
        content.id = `translation-${lang}`;

        if(result.targetOS === "ANDROID") {
            content.textContent = formatAndroidPreview(translations);
        } else {
            content.innerHTML = formatIOSPreview(translations);
        }

        contentDiv.appendChild(content);
    });

    container.appendChild(tabsDiv);
    container.appendChild(contentDiv);


    const generateBtn = document.createElement('button');
    generateBtn.className = 'preview-generate';
    generateBtn.textContent = 'Generate Resource Files';
    generateBtn.style.backgroundColor='#0B559D'
    generateBtn.onclick = () => {
        if(result.targetOS === "ANDROID") {
            generateAndroidResources(result);
        } else {
            generateiOSResources(result);
        }
    };
    container.appendChild(generateBtn);
}

/*
 *utility  to switch between different localized strings  with their tabs eg en,us
 */
function switchTab(lang) {
    document.querySelectorAll('.preview-tab').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.translation-item').forEach(item => item.classList.remove('active'));
    document.querySelectorAll('.preview-tab').forEach(tab => {
        if (tab.textContent === lang) {
            tab.classList.add('active');
        }
    });
    document.getElementById(`translation-${lang}`).classList.add('active');
}

/*
 * utility to show ios localized strings in correct format
 */
function formatIOSPreview(translations) {
    return Object.entries(translations)
        .map(([key, value]) => `"${key}" = "${value}";`)
        .join('\n');
}

/*
 *utility to show android localized strings in correct format
 */
function formatAndroidPreview(translations) {
    return `<?xml version="1.0" encoding="utf-8"?>
<resources>
    ${Object.entries(translations)
        .map(([key, value]) => `<string name="${key}">${value}</string>`)
        .join('\n    ')}
    </resources>`;
}


/* List of valid language codes */
const validLanguages= ["ar","az","be","bg","bn","bs","fr","pl","ha","am","ga","ca","cs","da","es",
    "et","fa","fi","gl","it","iw","ja","jv","ka","km","ko","ky","my","nb",
    "ne","nl","no","pa","ro","ru","si","sk","sl","sq","sr","sv","sw","ta",
    "te","th","tr","vi","zh","zu","kn","kk","af","de","fil","hi","hr","hu",
    "hy","id","in","lo","lv","mk","ml","mn","pt","uk","ur","ms","is","el",
    "mr","haw","he","ku","la","lt","lb","so"];
