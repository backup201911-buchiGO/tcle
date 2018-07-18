package org.blogsite.youngsoft.json;


public class Json2xml {
    private String ENCODING = "EUC-KR";
    private String XMLDECLARE = "<?xml version=\"1.0\" encoding=\"" + ENCODING + "\"?>\n";
    private final String CDATA_START = "<![CDATA[";
    private final String CDATA_END = "]]>";

    /**
     * 기본 생성자
     * 기본 인코딩은 EUC-KR
     */
    public Json2xml(){
        ENCODING = "EUC-KR";
        XMLDECLARE = "<?xml version=\"1.0\" encoding=\"" + ENCODING + "\"?>\n";
    }

    /**
     * XML 인코딩을 ENCODING로 하는 생성자
     * @param ENCODING
     */
    public Json2xml(String ENCODING){
        this.ENCODING = ENCODING;
        XMLDECLARE = "<?xml version=\"1.0\" encoding=\"" + ENCODING + "\"?>\n";
    }

    /**
     * jsonText를 루트 노드 root로 하는 xml로 변환하여 StringBuffer에 담는다.
     * @param root          - 루트 노드
     * @param sb            - 변환된 xml을 저장할 StringBuffer
     * @param jsonText      - json 문자열
     * @throws Exception
     */
    public void convert(String root, StringBuffer sb, String jsonText) throws Exception {
        sb.append(XMLDECLARE);
        JSONObject obj;
        sb.append("<").append(root).append(">\n");

        try {
            if (jsonText != null) {
                if (jsonText.trim().startsWith("[") && jsonText.trim().endsWith("]")) {
                    JSONArray array = new JSONArray(jsonText);
                    for (int i = 0; i < array.length(); i++) {
                        handleNestedJSON(sb, array.getJSONObject(i));
                    }
                } else {
                    obj = new JSONObject(jsonText);
                    handleNestedJSON(sb, obj);
                }
            }
            sb.append("</").append(root).append(">");
        } catch (JSONException ex) {
            throw new Exception(ex.getLocalizedMessage());
        }
    }

    public String convertJSON2XML(String root, String sourceText)
            throws Exception
    {
        StringBuffer sb = new StringBuffer();

        sb.append(this.XMLDECLARE);
        if (!"".equals(root)) {
            sb.append("<").append(root).append(">\n");
        }
        try
        {
            if (sourceText != null) {
                if ((sourceText.trim().startsWith("[")) && (sourceText.trim().endsWith("]")))
                {
                    JSONArray array = new JSONArray(sourceText);
                    for (int i = 0; i < array.length(); i++) {
                        handleNestedJSON(sb, array.getJSONObject(i));
                    }
                }
                else
                {
                    JSONObject obj = new JSONObject(sourceText);
                    handleNestedJSON(sb, obj);
                }
            }
            if (!"".equals(root)) {
                sb.append("</").append(root).append(">\n");
            }
            return sb.toString();
        }
        catch (JSONException ex)
        {
            throw new Exception(ex.getLocalizedMessage());
        }
    }

    /*
     * JSONArray/String을 제외한 Object 처리
     */
    private void handleObject(StringBuffer sb, String name, Object v) {
        if (v instanceof Integer) {
            sb.append("<").append(name).append(">");
            sb.append(v);
            sb.append("</").append(name).append(">\n");
        } else if (v instanceof Double) {
            sb.append("<").append(name).append(">");
            sb.append(v);
            sb.append("</").append(name).append(">\n");
        } else if (v instanceof Long) {
            sb.append("<").append(name).append(">");
            sb.append(v);
            sb.append("</").append(name).append(">\n");
        } else if (v instanceof Boolean) {
            sb.append("<").append(name).append(">");
            sb.append(v);
            sb.append("</").append(name).append(">\n");
        } else {
            sb.append("<").append(name).append(">");
            sb.append(v);
            sb.append("</").append(name).append(">\n");
        }
    }

    /**
     * JSONArray 처리 루틴
     */
    private void handleNestedJSONArray(StringBuffer sb, String name, JSONArray obj) throws Exception {
        for (int i = 0; i < obj.length(); i++) {
            Object v = obj.get(i);

            if (v instanceof String) {
                String nodeName = name + String.valueOf(i);
                sb.append("<").append(nodeName).append(">\n");
                sb.append(CDATA_START).append(v).append(CDATA_END);
                sb.append("</").append(nodeName).append(">\n");
            } else if (v instanceof JSONArray) {
                handleNestedJSONArray(sb, name, (JSONArray) v);
            } else {
                handleObject(sb, name, v);
            }
        }
    }

    /**
     * JSONObject 처리 루틴
     */
    private void handleNestedJSON(StringBuffer sb, JSONObject obj) throws Exception {
        String[] names = JSONObject.getNames(obj);
        if (names != null && names.length > 0) {
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                name = escapeNumber(name);
                try {
                    Object value = obj.get(names[i]);
                    if (value instanceof JSONObject) {
                        sb.append("<").append(name).append(">");
                        handleNestedJSON(sb, (JSONObject) value);
                        sb.append("</").append(name).append(">\n");
                    } else if (value instanceof Integer) {
                        sb.append("<").append(name).append(">");
                        sb.append(obj.getInt(names[i]));
                        sb.append("</").append(name).append(">\n");
                    } else if (value instanceof Double) {
                        sb.append("<").append(name).append(">");
                        sb.append(obj.getDouble(names[i]));
                        sb.append("</").append(name).append(">\n");
                    } else if (value instanceof Long) {
                        sb.append("<").append(name).append(">");
                        sb.append(obj.getLong(names[i]));
                        sb.append("</").append(name).append(">\n");
                    } else if (value instanceof Boolean) {
                        sb.append("<").append(name).append(">");
                        sb.append(obj.getBoolean(names[i]));
                        sb.append("</").append(name).append(">\n");
                    } else if (value instanceof JSONArray) {
                        /*
                         * JSONArray인 경우 각각의 배열에 해당하는 Node 이름을
                         * 정의하는 부분은 적당히 수정한다.
                         */
                    	
                        JSONArray objects = (JSONArray) value;
                        
                        for (int j = 0; j < objects.length(); j++) {
                            sb.append("<").append(name).append(">\n");
                            Object v = objects.get(j);
                            if (v instanceof String) {
                                String nodeName = name + "_sub";
                                sb.append("<").append(nodeName).append(">\n");
                                sb.append(CDATA_START).append(v).append(CDATA_END);
                                sb.append("</").append(nodeName).append(">\n");
                            } else if (v instanceof JSONArray) {
                                handleNestedJSONArray(sb, name, (JSONArray) v);
                            } else{
                            	handleNestedJSON(sb, objects.getJSONObject(j));
                            }
                            sb.append("</").append(name).append(">\n");
                        }
                    } else if (value instanceof String) {
                        sb.append("<").append(name).append(">");
                        sb.append(CDATA_START).append(obj.getString(names[i])).append(CDATA_END);
                        sb.append("</").append(name).append(">\n");
                    }
                } catch (JSONException e) {
                    throw new Exception(e.getLocalizedMessage());
                }
            }
        }

    }

    private String escapeNumber(String name) {
        if (name != null && Character.isDigit(name.charAt(0))) {
            return "_" + name;
        }
        return name;
    }



}
