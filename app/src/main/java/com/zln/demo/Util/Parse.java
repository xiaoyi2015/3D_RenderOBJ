package com.zln.demo.Util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zln on 2016/12/6.
 */

public class Parse {

    private static final String TAG = "Parse";
    private final List<Vec3> vertices = new ArrayList<>();
    private final List<Vec3> normals = new ArrayList<>();
    private final List<Vec2> textures = new ArrayList<>();
    private final List<Point> points = new ArrayList<>();
    private final List<Index> indexes = new ArrayList<>();
    private Map<String, Integer> map = new HashMap<>();


    private Vec3 minPoint = new Vec3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    private Vec3 maxPoint = new Vec3(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

    public Parse(InputStream objInputStream) throws Exception {
        parseOBJ(objInputStream);
    }

    public int getIndexNumber() {
        return indexes.size();
    }

    public void parseOBJ(InputStream objInputStream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(objInputStream));
        String line;
        List<String[]> tempFaceTokens = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0 || line.startsWith("#")) {
                continue;
            }
            String[] tokens = line.split(" ");
            switch (tokens[0]) {
                case "v":
                    final Vec3 position = new Vec3(Arrays.copyOfRange(tokens, 1, tokens.length));
                    vertices.add(position);
                    updateMaxMin(position);
                    break;
                case "vn":
                    normals.add(new Vec3(Arrays.copyOfRange(tokens, 1, tokens.length)).normalize());
                    break;
                case "vt":
                    textures.add(new Vec2(Arrays.copyOfRange(tokens, 1, tokens.length)));
                    break;
                case "f":
                    tempFaceTokens.add(Arrays.copyOfRange(tokens, 1, tokens.length));
                    break;
                default:
                    Log.e(TAG, "unknown element: " + tokens[0]);
            }
        }
        //归一model到(-1,1)
        normalizedPosition();

        parseFace(tempFaceTokens);

    }

    private void updateMaxMin(Vec3 position) {
        minPoint = minPoint.min(position);
        maxPoint = maxPoint.max(position);
    }

    private void normalizedPosition() {
        Vec3 length = maxPoint.subtract(minPoint);
        Float d = length.maxComponent() / 2;
        //length = length.div(d);
        d *= 1.5f;
        Vec3 centre = minPoint.mid(maxPoint);
        for (int i = 0; i < vertices.size(); i++) {
            vertices.set(i, vertices.get(i).subtract(centre).div(d));
        }
    }

    private void parseFace(List<String[]> tempFaceTokens) {
        for (String[] token : tempFaceTokens) {
            if (token.length == 3 || token.length == 4) {
                parseFace(token[0], token[1], token[2]);
                if (token.length == 4) {
                    parseFace(token[0], token[2], token[3]);
                }
            } else {
                Log.e(TAG, "can only handle 3 or 4 points in one face");
            }
        }
    }

    private void parseFace(String p0String, String p1String, String p2String) {
        Index index = new Index();
        getPoint(p0String, 1 ,index);
        getPoint(p1String, 2, index);
        getPoint(p2String, 3, index);
        indexes.add(index);

    }

    private void getPoint(String pointString, int key, Index index) {
        Integer id = map.get(pointString);

        if(id == null) {
            map.put(pointString, map.size());
            String[] indexes = pointString.split("/");
            Point point = new Point();

            point.setPosition(vertices.get(Integer.parseInt(indexes[0]) - 1));
            if (indexes[1].length() != 0) {
                point.setTexture(textures.get(Integer.parseInt(indexes[1]) - 1));
            } else {
                point.setTexture(new Vec2(0f, 0f));
            }
            if (indexes.length == 3) {
                point.setNormal(normals.get(Integer.parseInt(indexes[2]) - 1));
            }
            points.add(point);

        }
        if(key == 1)
            index.a = map.get(pointString);
        if(key == 2)
            index.b = map.get(pointString);
        if(key == 3)
            index.c = map.get(pointString);

    }

    public ByteBuffer getVertexes() {
        Log.v("zln- vertex:", points.size()+"");
        for(int i = 0; i < points.size(); i ++){
            Log.v("zln- vertex:", points.get(i).getPosition().x + " " + points.get(i).getPosition().y + " " + points.get(i).getPosition().z);
        }
        ByteBuffer bb = ByteUtil.genDirectBuffer(points.size() * Point.SIZE_AS_BYTE);
        for (Point p : points) {
            bb.put(p.toByteBuffer());
        }
        bb.flip();
        return bb;
    }

    public ByteBuffer getIndexes() {
        Log.v("zln- index:", indexes.size()+"");
        for(int i = 0; i < indexes.size(); i ++){
            Log.v("zln- index:", indexes.get(i).a + " " + indexes.get(i).b + " " + indexes.get(i).c);

        }
        ByteBuffer bb = ByteUtil.genDirectBuffer(indexes.size() * Index.SIZE_AS_BYTE);
        for (Index i : indexes) {
            bb.put(i.toByteBuffer());
        }
        bb.flip();
        return bb;
    }

}
