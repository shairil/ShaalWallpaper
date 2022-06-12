from bs4 import BeautifulSoup
import requests
from io import BytesIO
from PIL import Image, UnidentifiedImageError


def getId(imgURL):
    imgURL = getHighImgResolution(imgURL)
    return int(imgURL[35:41])

def getHighImgResolution(imgURL):
    img = imgURL.replace("thumb-", "")
    try:
        image_raw = requests.get(img)
        #print(len(image_raw.content) / 1024)
        image = Image.open(BytesIO(image_raw.content))
        return img

    except UnidentifiedImageError:
        return imgURL


def getCount():
    url = "https://mobile.alphacoders.com/by-category/3"
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'lxml')
    response.close()

    lastPage = soup.findAll('div', attrs={'class': 'active custom-nav-tabs-element'})
    s = 'active custom-nav-tabs-element">'
    end = str(lastPage[0]).index(' Phone')
    start = str(lastPage[0]).index(s)
    count = (int(str(lastPage[0])[start + len(s):end]))
    return count

def anime_wallpaper(n):
    url = "https://mobile.alphacoders.com/by-category/3?page="+str(n)
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'lxml')
    response.close()

    movieList = soup.select('div.item a ')
    resolution = soup.select('div.item span')
    res = []

    for i in resolution:
        ind = str(i).index("resolution-info\">")
        res.append(str(i)[ind+19:-7].strip())

    title = []
    for i in movieList:
        ind = str(i).index("title=")
        title.append(str(i)[ind+7:-24])

    imgURL = []
    id = []
    for i in movieList:
        ind = str(i).index("src=")
        end = str(i).index("\" style")
        img = str(i)[ind+5:end]
        imgURL.append(img)
        id.append(getId(img))

    return title, imgURL, res, id