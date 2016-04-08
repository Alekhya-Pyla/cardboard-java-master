/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.vrtoolkit.cardboard.samples.treasurehunt;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.google.vrtoolkit.cardboard.audio.CardboardAudioEngine;
import com.google.vrtoolkit.cardboard.sensors.internal.Vector3d;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * A Cardboard sample application.
 */
public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer {
  private static final String TAG = "MainActivity";

  float datX = 0,datY = 0,datZ = 0;
  int datX2 = 1,datY2 = 1,datZ2 = 1;
  private static final float Z_NEAR = 0.1f;
  private static final float Z_FAR = 100.0f;

  private static final float CAMERA_Z = 0.01f;
  private static final float TIME_DELTA = 0.3f;

  private static final float YAW_LIMIT = 0.12f;
  private static final float PITCH_LIMIT = 0.12f;

  private static final int COORDS_PER_VERTEX = 3;

  // We keep the light always position just above the user.
  private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[] {0.0f, 2.0f, 0.0f, 1.0f};

  private static final float MIN_MODEL_DISTANCE = 3.0f;
  private static final float MAX_MODEL_DISTANCE = 7.0f;

  private static final String SOUND_FILE = "cube_sound.wav";

  private final float[] lightPosInEyeSpace = new float[4];

  GetSurfaceCoords coords;

  private FloatBuffer cubeVertices;
  private FloatBuffer cubeColors;
  private FloatBuffer cubeNormals;

  private int cubeProgram;

  private int cubePositionParam;
  private int cubeNormalParam;
  private int cubeColorParam;
  private int cubeModelParam;
  private int cubeModelViewParam;
  private int cubeModelViewProjectionParam;
  private int cubeLightPosParam;

  private float[] modelCube;
  private float[] camera;
  private float[] view;
  private float[] headView;
  private float[] modelViewProjection;
  private float[] modelView;
  private float[] modelFloor;

  private float[] modelPosition;
  private float[] headRotation;
  String newData = "";
  public static final String IP = "http://192.168.1.100:9090/";

  private int score = 0;
  private float objectDistance = MAX_MODEL_DISTANCE / 2.0f;

  private Vibrator vibrator;
  private CardboardOverlayView overlayView;

  private CardboardAudioEngine cardboardAudioEngine;
  private volatile int soundId = CardboardAudioEngine.INVALID_ID;

  /**
   * Converts a raw text file, saved as a resource, into an OpenGL ES shader.
   *
   * @param type The type of shader we will be creating.
   * @param resId The resource ID of the raw text file about to be turned into a shader.
   * @return The shader object handler.
   */
  private int loadGLShader(int type, int resId) {
    Log.d("MainActivity","LoadSFLhader : "+resId);
    String code = readRawTextFile(resId);
    int shader = GLES20.glCreateShader(type);
    GLES20.glShaderSource(shader, code);
    GLES20.glCompileShader(shader);

    // Get the compilation status.
    final int[] compileStatus = new int[1];
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

    // If the compilation failed, delete the shader.
    if (compileStatus[0] == 0) {
      Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
      GLES20.glDeleteShader(shader);
      shader = 0;
    }

    if (shader == 0) {
      throw new RuntimeException("Error creating shader.");
    }

    return shader;
  }

  /**
   * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
   *
   * @param label Label to report in case of error.
   */
  private static void checkGLError(String label) {
    int error;
    while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
      Log.e(TAG, label + ": glError " + error);
      throw new RuntimeException(label + ": glError " + error);
    }
  }

  /**
   * Sets the view to our CardboardView and initializes the transformation matrices we will use
   * to render our scene.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("MainActivity", "Length : " + WorldLayoutData.CUBE_COORDS.length + "");
    coords=new GetSurfaceCoords();
    setContentView(R.layout.common_ui);
    CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
    cardboardView.setRestoreGLStateEnabled(false);
    cardboardView.setRenderer(this);
    setCardboardView(cardboardView);

    modelCube = new float[16];
    camera = new float[16];
    view = new float[16];
    modelViewProjection = new float[16];
    modelView = new float[16];
    modelFloor = new float[16];
    // Model first appears directly in front of user.
    modelPosition = new float[] {0.0f, 0.0f, -MAX_MODEL_DISTANCE / 2.0f};
    headRotation = new float[4];
    headView = new float[16];
    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    overlayView = (CardboardOverlayView) findViewById(R.id.overlay);
    overlayView.show3DToast("Pull the magnet when you find an object.");

    // Initialize 3D audio engine.
    cardboardAudioEngine =
        new CardboardAudioEngine(getAssets(), CardboardAudioEngine.RenderingQuality.HIGH);
//    GetSurfaceCoords getSurfaceCoords = new GetSurfaceCoords();
//    getSurfaceCoords.updatePointSet(new Vector3d(0,0,0));
//    String tmp = "";
//    for(int i=0;i<72;i++)
//      tmp += getSurfaceCoords.coordSet[i]+" : ";
//    Log.d(TAG,tmp);
      new AsyncTask<Void,Integer,Void>()
      {
        @Override
        protected Void doInBackground(Void... voids) {
          while(true) {
            String response = "";
            try {
              URL url = new URL(IP);
              URLConnection uc = url.openConnection();
              uc.setUseCaches(true);
              uc.setReadTimeout(15);
              uc.setConnectTimeout(15);
              BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
              String inputLine;
              while ((inputLine = in.readLine()) != null) {
                response += inputLine;
              }
              in.close();
            } catch (Exception e) {
              try {
                String dat = e.getMessage().substring(24);
                String[] lineVector = dat.split(",");
                Integer[] dataI = {Integer.parseInt(lineVector[0]), Integer.parseInt(lineVector[1]), Integer.parseInt(lineVector[2])};
                publishProgress(dataI);
              }
              catch (Exception e2)
              {
//                Log.d(TAG,e2.getMessage());
              }
            }
          }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
//          Log.d(TAG, values[0].toString() + " : " + values[1] + " : " + values[2]);
          datX = values[0]/100000.0f;
          datY = values[1]/-100000.0f;
          datZ = values[2]/10000.0f;
//          Log.d(TAG, values[0].toString() + " : " + values[1] + " : " + values[2]);
          super.onProgressUpdate(values);
        }
      }.execute();
  }

  @Override
  public void onPause() {
    cardboardAudioEngine.pause();
    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    cardboardAudioEngine.resume();
  }

  @Override
  public void onRendererShutdown() {
    Log.i(TAG, "onRendererShutdown");
  }

  @Override
  public void onSurfaceChanged(int width, int height) {
    Log.i(TAG, "onSurfaceChanged");
  }

  /**
   * Creates the buffers we use to store information about the 3D world.
   *
   * <p>OpenGL doesn't use Java arrays, but rather needs data in a format it can understand.
   * Hence we use ByteBuffers.
   *
   * @param config The EGL configuration used when creating the surface.
   */
  @Override
  public void onSurfaceCreated(EGLConfig config) {
    Log.i(TAG, "onSurfaceCreated");
    setObject();
  }

  private void setObject()
  {
    GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f); // Dark background so text shows up well.
    coords.updatePointSet(new Vector3d(datX, datY, datZ));
    ByteBuffer bbVertices = ByteBuffer.allocateDirect(coords.coordSet.length * 4);
    bbVertices.order(ByteOrder.nativeOrder());
    cubeVertices = bbVertices.asFloatBuffer();
    cubeVertices.put(coords.coordSet);
    cubeVertices.position(0);

    ByteBuffer bbColors = ByteBuffer.allocateDirect(coords.coordCol.length * 4);
    bbColors.order(ByteOrder.nativeOrder());
    cubeColors = bbColors.asFloatBuffer();
    cubeColors.put(coords.coordCol);
    cubeColors.position(0);

    ByteBuffer bbNormals = ByteBuffer.allocateDirect(coords.coordNorm.length * 4);
    bbNormals.order(ByteOrder.nativeOrder());
    cubeNormals = bbNormals.asFloatBuffer();
    cubeNormals.put(coords.coordNorm);
    cubeNormals.position(0);

    int vertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.light_vertex);
    int passthroughShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment);

    cubeProgram = GLES20.glCreateProgram();
    GLES20.glAttachShader(cubeProgram, vertexShader);
    GLES20.glAttachShader(cubeProgram, passthroughShader);
    GLES20.glLinkProgram(cubeProgram);
    GLES20.glUseProgram(cubeProgram);

    checkGLError("Cube program");

    cubePositionParam = GLES20.glGetAttribLocation(cubeProgram, "a_Position");
    cubeNormalParam = GLES20.glGetAttribLocation(cubeProgram, "a_Normal");
    cubeColorParam = GLES20.glGetAttribLocation(cubeProgram, "a_Color");

    cubeModelParam = GLES20.glGetUniformLocation(cubeProgram, "u_Model");
    cubeModelViewParam = GLES20.glGetUniformLocation(cubeProgram, "u_MVMatrix");
    cubeModelViewProjectionParam = GLES20.glGetUniformLocation(cubeProgram, "u_MVP");
    cubeLightPosParam = GLES20.glGetUniformLocation(cubeProgram, "u_LightPos");

    GLES20.glEnableVertexAttribArray(cubePositionParam);
    GLES20.glEnableVertexAttribArray(cubeNormalParam);
    GLES20.glEnableVertexAttribArray(cubeColorParam);

    checkGLError("Cube program params");
    updateModelPosition();

    checkGLError("onSurfaceCreated");
  }

  /**
   * Updates the cube model position.
   */
  private void updateModelPosition() {
//    Log.d("MainActivity", "UpdateModelPosition");
    Matrix.setIdentityM(modelCube, 0);
    Matrix.translateM(modelCube, 0, modelPosition[0], modelPosition[1], modelPosition[2]);

    // Update the sound location to match it with the new cube position.
    if (soundId != CardboardAudioEngine.INVALID_ID) {
      cardboardAudioEngine.setSoundObjectPosition(
          soundId, modelPosition[0], modelPosition[1], modelPosition[2]);
    }
    checkGLError("updateCubePosition");
  }

  /**
   * Converts a raw text file into a string.
   *
   * @param resId The resource ID of the raw text file about to be turned into a shader.
   * @return The context of the text file, or null in case of error.
   */
  private String readRawTextFile(int resId) {
    InputStream inputStream = getResources().openRawResource(resId);
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
      }
      reader.close();
      return sb.toString();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Prepares OpenGL ES before we draw a frame.
   *
   * @param headTransform The head transformation in the new frame.
   */
  @Override
  public void onNewFrame(HeadTransform headTransform) {
    double dist = (Math.sqrt((datX - coords.lastPoint.x)*(datX - coords.lastPoint.x)
            + (datY - coords.lastPoint.y)*(datY - coords.lastPoint.y) + (datZ - coords.lastPoint.z)*(datZ - coords.lastPoint.z)));
//    Log.d(TAG,"::::::"+dist+"");
    if(dist>0.001) {
//      Log.d(TAG, "I am in");
      setObject();
    }

//    Log.d("MainActivity", "onnewFrame");
    // Build the Model part of the ModelView matrix.
//    Matrix.rotateM(modelCube, 0, TIME_DELTA, 0.5f, 0.5f, 1.0f);

    // Build the camera matrix and apply it to the ModelView.
    Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

    headTransform.getHeadView(headView, 0);

    // Update the 3d audio engine with the most recent head rotation.
    headTransform.getQuaternion(headRotation, 0);
    cardboardAudioEngine.setHeadRotation(
            headRotation[0], headRotation[1], headRotation[2], headRotation[3]);

    checkGLError("onReadyToDraw");
  }

  /**
   * Draws a frame for an eye.
   *
   * @param eye The eye to render. Includes all required transformations.
   */
  @Override
  public void onDrawEye(Eye eye) {
//    Log.d("MainActivity", "onDrawEye");
    GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    checkGLError("colorParam");

    // Apply the eye transformation to the camera.
    Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

    // Set the position of the light
    Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

    // Build the ModelView and ModelViewProjection matrices
    // for calculating cube position and light.
    float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);
    Matrix.multiplyMM(modelView, 0, view, 0, modelCube, 0);
    Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);

    drawCube();

    // Set modelView for the floor, so we draw floor in the correct location
    Matrix.multiplyMM(modelView, 0, view, 0, modelFloor, 0);
    Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
  }

  @Override
  public void onFinishFrame(Viewport viewport) {}

  /**
   * Draw the cube.
   *
   * <p>We've set all of our transformation matrices. Now we simply pass them into the shader.
   */
  public void drawCube() {
    GLES20.glUseProgram(cubeProgram);

    GLES20.glUniform3fv(cubeLightPosParam, 1, lightPosInEyeSpace, 0);

    // Set the Model in the shader, used to calculate lighting
    GLES20.glUniformMatrix4fv(cubeModelParam, 1, false, modelCube, 0);

    // Set the ModelView in the shader, used to calculate lighting
    GLES20.glUniformMatrix4fv(cubeModelViewParam, 1, false, modelView, 0);

    // Set the position of the cube
    GLES20.glVertexAttribPointer(
            cubePositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, cubeVertices);

    // Set the ModelViewProjection matrix in the shader.
    GLES20.glUniformMatrix4fv(cubeModelViewProjectionParam, 1, false, modelViewProjection, 0);

    // Set the normal positions of the cube, again for shading
    GLES20.glVertexAttribPointer(cubeNormalParam, 3, GLES20.GL_FLOAT, false, 0, cubeNormals);
    GLES20.glVertexAttribPointer(cubeColorParam, 4, GLES20.GL_FLOAT, false, 0, cubeColors);

    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, coords.coordSet.length/3);
    checkGLError("Drawing cube");
  }
  /**
   * Called when the Cardboard trigger is pulled.
   */
  @Override
  public void onCardboardTrigger() {
    Log.i(TAG, "onCardboardTrigger");

    if (isLookingAtObject()) {
      score++;
      overlayView.show3DToast("Found it! Look around for another one.\nScore = " + score);
      hideObject();
    } else {
      overlayView.show3DToast("Look around to find the object!");
    }

    // Always give user feedback.
    vibrator.vibrate(50);
  }

  /**
   * Find a new random position for the object.
   *
   * <p>We'll rotate it around the Y-axis so it's out of sight, and then up or down by a little bit.
   */
  private void hideObject() {
    float[] rotationMatrix = new float[16];
    float[] posVec = new float[4];

    // First rotate in XZ plane, between 90 and 270 deg away, and scale so that we vary
    // the object's distance from the user.
    float angleXZ = (float) Math.random() * 180 + 90;
    Matrix.setRotateM(rotationMatrix, 0, angleXZ, 0f, 1f, 0f);
    float oldObjectDistance = objectDistance;
    objectDistance =
        (float) Math.random() * (MAX_MODEL_DISTANCE - MIN_MODEL_DISTANCE) + MIN_MODEL_DISTANCE;
    float objectScalingFactor = objectDistance / oldObjectDistance;
    Matrix.scaleM(rotationMatrix, 0, objectScalingFactor, objectScalingFactor, objectScalingFactor);
    Matrix.multiplyMV(posVec, 0, rotationMatrix, 0, modelCube, 12);

    float angleY = (float) Math.random() * 80 - 40; // Angle in Y plane, between -40 and 40.
    angleY = (float) Math.toRadians(angleY);
    float newY = (float) Math.tan(angleY) * objectDistance;

    modelPosition[0] = posVec[0];
    modelPosition[1] = newY;
    modelPosition[2] = posVec[2];

    updateModelPosition();
  }

  /**
   * Check if user is looking at object by calculating where the object is in eye-space.
   *
   * @return true if the user is looking at the object.
   */
  private boolean isLookingAtObject() {
    float[] initVec = {0, 0, 0, 1.0f};
    float[] objPositionVec = new float[4];

    // Convert object space to camera space. Use the headView from onNewFrame.
    Matrix.multiplyMM(modelView, 0, headView, 0, modelCube, 0);
    Matrix.multiplyMV(objPositionVec, 0, modelView, 0, initVec, 0);

    float pitch = (float) Math.atan2(objPositionVec[1], -objPositionVec[2]);
    float yaw = (float) Math.atan2(objPositionVec[0], -objPositionVec[2]);

    return Math.abs(pitch) < PITCH_LIMIT && Math.abs(yaw) < YAW_LIMIT;
  }
}
