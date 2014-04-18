/**
 * Copyright (c) 2014, Bait Al-Hikma LTD. All rights reserved.
 * 
 * This file is part of Waseda Connect.
 *
 * Waseda Connect is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Waseda Connect is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Waseda Connect. If not, see <http://www.gnu.org/licenses/>.
 */
package com.bai.android.data.arcamera;

import java.util.HashMap;
import java.util.Map;

public class Particle {
  private Map<String, String> funcParts = new HashMap<String, String>();
  private Map<String, String> meanParts = new HashMap<String, String>();

  // constructor
  public Particle() {
    funcParts.put("��", "");
    funcParts.put("��", "");
    funcParts.put("����", "");
    funcParts.put("��", "");

    meanParts.put("������", "myself");
    meanParts.put("�~����", "myself");
    meanParts.put("��", "too");
    meanParts.put("��", "");
    meanParts.put("��������", "because");
    meanParts.put("���񂾂���", "because");
    meanParts.put("����", "because");
    meanParts.put("�킢��", "!");
    meanParts.put("�킢��", "!");
    meanParts.put("�킢�̂�", "!");
    meanParts.put("��", "!?");
    meanParts.put("��", "?");
    meanParts.put("����", "or ...?");
    meanParts.put("������","??");
    meanParts.put("����", "or ...?");
    meanParts.put("����",", right?");
    meanParts.put("����", "");
    meanParts.put("����", "Really?");
    meanParts.put("����","");
    meanParts.put("���Ȃ�","");
    meanParts.put("��","?");
    meanParts.put("����", "?");
    meanParts.put("����", "");
    meanParts.put("��", "!");
    meanParts.put("����", "");
    meanParts.put("��", "!");
    meanParts.put("����", "");
    meanParts.put("����", "");
    meanParts.put("����", "and");
    meanParts.put("����", "?");
    meanParts.put("����", "and");
    meanParts.put("����","?");
    meanParts.put("����", "");
    meanParts.put("���Ă�","");
    meanParts.put("��", "");
    meanParts.put("�Ă�","");
    meanParts.put("��", "in");
    meanParts.put("�ł�����", "by");
    meanParts.put("�Ƃ�", "in other words");
    meanParts.put("�Ƃ��","I heard");
    meanParts.put("�ǂ���","not at all");
    meanParts.put("��", "!");
    meanParts.put("��","at");
    meanParts.put("�ɂ�", "in");
    meanParts.put("��",", right?");
    meanParts.put("�˂�",", right?");
    meanParts.put("�˂�", "!");
    meanParts.put("��", "'s");
    meanParts.put("�̂�", "!");
    meanParts.put("�̂�", "");
    meanParts.put("�̂�", "");
    meanParts.put("��", "");
    meanParts.put("�΂�", "!");
    meanParts.put("��", "to");
    meanParts.put("�w", "to");
    meanParts.put("������","");
    meanParts.put("���̂�","");
    meanParts.put("����","");
    meanParts.put("��", "and");
    meanParts.put("���", "");
    meanParts.put("��","!");
    meanParts.put("��", "!");
    meanParts.put("�킢", "!");
    meanParts.put("�킦", "!");
    meanParts.put("���", "!");
    meanParts.put("��", "and");
    meanParts.put("�Ƃ�", "or");
    meanParts.put("���̂�","because");
    meanParts.put("�����","because");
    meanParts.put("�̂�","because");
    meanParts.put("�̂�","instead");
  }
  public String particleMeaning(String particle) {
    String part = particle;
    String meaning="";
    // if particle can be translated into some meaningful word(s) (found among the hashmap keys)
    if (meanParts.containsKey(part)){
      meaning = meanParts.get(part); // get meaning
    } else if (funcParts.containsKey(part)){
      meaning = funcParts.get(part); // get functional stuff TODO
    } else {
      meaning = part;
    }
    return meaning;
  }
}
