/*
 * Copyright 2014 Google Inc. All Rights Reserved.

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

/**
 * Contains vertex, normal and color data.
 */
public final class WorldLayoutData {

  public static final float[] CUBE_COORDS = new float[] {
      // Front face
      -1.0f, 1.0f, 1.0f,
      -1.0f, -1.0f, 1.0f,
      1.0f, 1.0f, 1.0f,
      -1.0f, -1.0f, 1.0f,
      1.0f, -1.0f, 1.0f,
      1.0f, 1.0f, 1.0f,
//
      // Right facec
//      1.0f, 1.0f, 1.0f,
//      1.0f, -1.0f, 1.0f,
//      1.0f, 1.0f, -1.0f,
//      1.0f, -1.0f, 1.0f,
//      1.0f, -1.0f, -1.0f,
//      1.0f, 1.0f, -1.0f,
//
      // Back face
      1.0f, 1.0f, -1.0f,
      1.0f, -1.0f, -1.0f,
      -1.0f, 1.0f, -1.0f,
      1.0f, -1.0f, -1.0f,
      -1.0f, -1.0f, -1.0f,
      -1.0f, 1.0f, -1.0f,
//
//      // Left face
//      -1.0f, 1.0f, -1.0f,
//      -1.0f, -1.0f, -1.0f,
//      -1.0f, 1.0f, 1.0f,
//      -1.0f, -1.0f, -1.0f,
//      -1.0f, -1.0f, 1.0f,
//      -1.0f, 1.0f, 1.0f,
//
//      // Top face
      -1.0f, 1.0f, -1.0f,
      -1.0f, 1.0f, 1.0f,
      1.0f, 1.0f, -1.0f,
      -1.0f, 1.0f, 1.0f,
      1.0f, 1.0f, 1.0f,
      1.0f, 1.0f, -1.0f,

      // Bottom face
      1.0f, -1.0f, -1.0f,
      1.0f, -1.0f, 1.0f,
      -1.0f, -1.0f, -1.0f,
      1.0f, -1.0f, 1.0f,
      -1.0f, -1.0f, 1.0f,
      -1.0f, -1.0f, -1.0f,
  };

  public static final float[] CUBE_COLORS = new float[] {
      // front, green
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,

      // right, blue
//      0.0f, 0.3398f, 0.9023f, 1.0f,
//      0.0f, 0.3398f, 0.9023f, 1.0f,
//      0.0f, 0.3398f, 0.9023f, 1.0f,
//      0.0f, 0.3398f, 0.9023f, 1.0f,
//      0.0f, 0.3398f, 0.9023f, 1.0f,
//      0.0f, 0.3398f, 0.9023f, 1.0f,
//
      // back, also green
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
//
//      // left, also blue
//      0.0f, 0.3398f, 0.9023f, 1.0f,
//      0.0f, 0.3398f, 0.9023f, 1.0f,
//      0.0f, 0.3398f, 0.9023f, 1.0f,
//      0.0f, 0.3398f, 0.9023f, 1.0f,
//      0.0f, 0.3398f, 0.9023f, 1.0f,
//      0.0f, 0.3398f, 0.9023f, 1.0f,
//
//      // top, red

      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,

      // bottom, also red

      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
      0.8f, 1.0f, 0.2f, 1.0f,
  };

  public static final float[] CUBE_NORMALS = new float[] {
          // Front face
          0.0f, 1.0f, 1.0f,
          0.0f, -1.0f, 1.0f,
          0.0f, 1.0f, 1.0f,
          0.0f, -1.0f, 1.0f,
          0.0f, -1.0f, 1.0f,
          0.0f, 1.0f, 1.0f,
//
          // Right facec
//      1.0f, 1.0f, 1.0f,
//      1.0f, -1.0f, 1.0f,
//      1.0f, 1.0f, -1.0f,
//      1.0f, -1.0f, 1.0f,
//      1.0f, -1.0f, -1.0f,
//      1.0f, 1.0f, -1.0f,
//
          // Back face
          0.0f, 1.0f, -1.0f,
          0.0f, -1.0f, -1.0f,
          0.0f, 1.0f, -1.0f,
          0.0f, -1.0f, -1.0f,
          0.0f, -1.0f, -1.0f,
          0.0f, 1.0f, -1.0f,
//
//      // Left face
//      -1.0f, 1.0f, -1.0f,
//      -1.0f, -1.0f, -1.0f,
//      -1.0f, 1.0f, 1.0f,
//      -1.0f, -1.0f, -1.0f,
//      -1.0f, -1.0f, 1.0f,
//      -1.0f, 1.0f, 1.0f,
//
//      // Top face
          0.0f, 1.0f, -1.0f,
          0.0f, 1.0f, 1.0f,
          0.0f, 1.0f, -1.0f,
          0.0f, 1.0f, 1.0f,
          0.0f, 1.0f, 1.0f,
          0.0f, 1.0f, -1.0f,

          // Bottom face
          0.0f, -1.0f, -1.0f,
          0.0f, -1.0f, 1.0f,
          0.0f, -1.0f, -1.0f,
          0.0f, -1.0f, 1.0f,
          0.0f, -1.0f, 1.0f,
          0.0f, -1.0f, -1.0f,
  };
}
