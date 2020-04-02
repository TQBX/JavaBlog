![index](E:\1JavaBlog\maven\pic\index.png)

```java

	//查找人气旅游
	@Override
    public List<Route> findCount(int size) {
        return routeDao.findByCount(size);
    }
	//查找最新旅游
    @Override
    public List<Route> findDate(int size) {
        return routeDao.findByRdate(size);
    }
	//查找主题旅游
    @Override
    public List<Route> findTheme(int size) {
        return routeDao.findByTheme(size);
    }
```

```java

	 /**
     * 根据不同主题查询
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void findCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String categoryStr = request.getParameter("category");
        List<Route> list;
        //页面显示的数量
        int size = 4;
        if("isThemeTour".equals(categoryStr)){
            list = routeService.findTheme(size);
        }else if ("rdate".equals(categoryStr)){
            list = routeService.findDate(size);
        }else {
            list = routeService.findCount(size);
        }
        writeValue(list,response);
    }
```

