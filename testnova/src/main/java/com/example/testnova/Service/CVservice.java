package com.example.testnova.Service;

import com.example.testnova.Model.Candidat;
import com.example.testnova.Model.Cvanalyse;
import com.example.testnova.Model.Experience;
import com.example.testnova.Model.Skill;
import com.example.testnova.Repository.CandidatRepository;
import com.example.testnova.Repository.cvanalyseRep;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class CVservice {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final cvanalyseRep cvanalyseRepository;
    private final CandidatRepository candidatRepository;

    public CVservice(ChatClient.Builder chatClientbuilder, cvanalyseRep cvanalyseRepository,
            CandidatRepository candidatRepository) {
        this.chatClient = chatClientbuilder.build();
        this.objectMapper = new ObjectMapper();
        this.cvanalyseRepository = cvanalyseRepository;
        this.candidatRepository = candidatRepository;
    }

    public Object analysecvoffre(String cvtext, Object offre, Long userId) {
        System.out.println("[CVService] Début de l'analyse IA pour userId: " + userId);

        String prompt = """
                Tu es un expert en ressources humaines et en intelligence artificielle.
                Analyse le CV ci-dessous **en le comparant à l'offre d'emploi fournie**.
                Ton objectif est d'évaluer le niveau d'adéquation entre le profil et l'offre.

                === CV TEXTE ===
                %s

                === OFFRE D'EMPLOI ===
                %s

                Réponds UNIQUEMENT avec un **JSON pur et valide** (pas de texte, pas de markdown, pas de backticks).
                Si une information est manquante, laisse le champ vide.
                Ne commente pas et ne reformule pas le JSON.

                {
                  "resume": "Résumé intelligent du profil en 2 à 3 phrases",
                  "skills": [
                    {
                      "name": "Compétence",
                      "level": "beginner|intermediate|advanced|expert",
                      "type": "hardSkills|softSkills"
                    }
                  ],
                  "experience": [
                    {
                      "company": "Nom de l'entreprise",
                      "role": "Poste occupé",
                      "year": "Année ou période",
                      "duration": "Durée en mois ou années",
                      "competences": ["Compétence utilisée 1", "Compétence utilisée 2"]
                    }
                  ],
                  "matching": {
                    "score": "Pourcentage de correspondance global (0-100)",
                    "matchedSkills": ["Compétence du CV présente dans l'offre"],
                    "missingSkills": ["Compétence demandée dans l'offre mais absente du CV"],
                    "comment": "Courte explication du matching"
                  }
                }
                """.formatted(cvtext, offre.toString());

        try {
            var response = chatClient.prompt()
                    .user(Objects.requireNonNull(prompt))
                    .call();

            String jsonString = Objects.requireNonNull(response.content());
            System.out.println("[CVService] Analyse IA terminée");
            System.out.println(
                    "[CVService] JSON brut: " + jsonString.substring(0, Math.min(200, jsonString.length())) + "...");

            String cleanJson = extractJsonFromResponse(jsonString);
            System.out.println(
                    "[CVService] JSON nettoyé: " + cleanJson.substring(0, Math.min(100, cleanJson.length())) + "...");

            @SuppressWarnings("unchecked")
            Map<String, Object> parsedJson = objectMapper.readValue(cleanJson, Map.class);

            System.out.println("[CVService] JSON parsé avec succès");

            // 🟦 Matching info
            Object matchingObj = parsedJson.get("matching");
            if (matchingObj instanceof Map<?, ?> matchMap) {
                Map<String, Object> matching = new java.util.HashMap<>();
                matching.put("score", matchMap.get("score"));
                matching.put("matchedSkills", matchMap.get("matchedSkills"));
                matching.put("missingSkills", matchMap.get("missingSkills"));
                matching.put("comment", matchMap.get("comment"));
                parsedJson.put("matching", matching);
            }

            // 🟦 Map parsed JSON to entity
            Cvanalyse cvanalyse = new Cvanalyse();

            // 🟦 Associer le candidat au CV
         if (userId != null) {
    Candidat candidat = candidatRepository.findById(userId).orElse(null);
    if (candidat != null) {
        cvanalyse.setUser(candidat);
        System.out.println("[CVService] CV associé au candidat: " + candidat.getEmail());
        
        // 🆕 Sauvegarder le titre du poste de l'offre dans le candidat
        if (offre instanceof Map<?, ?> offreMap) {
            Object titrePoste = offreMap.get("title");
            if (titrePoste != null) {
                candidat.setPosteRecherche(titrePoste.toString());
                candidatRepository.save(candidat);
                System.out.println("[CVService] Poste recherché mis à jour: " + titrePoste);
            }
        }
    } else {
        System.out.println("[CVService] Candidat non trouvé pour userId: " + userId);
    }
}

            Object resumeObj = parsedJson.get("resume");
            if (resumeObj != null)
                cvanalyse.setResume(resumeObj.toString());

            // Skills
            List<Skill> skillEntities = new ArrayList<>();
            Object skillsObj = parsedJson.get("skills");
            if (skillsObj instanceof List<?> skillsList) {
                for (Object s : skillsList) {
                    if (s instanceof Map<?, ?> smap) {
                        Skill skill = new Skill();
                        Object name = smap.get("name");
                        Object level = smap.get("level");
                        Object type = smap.get("type");
                        if (name != null)
                            skill.setName(name.toString());
                        if (level != null)
                            skill.setLevel(level.toString());
                        if (type != null)
                            skill.setType(type.toString());
                        skillEntities.add(skill);
                    }
                }
            }
            cvanalyse.setSkills(skillEntities);

            // Experiences
            List<Experience> expEntities = new ArrayList<>();
            Object expObj = parsedJson.get("experience");
            if (expObj instanceof List<?> expList) {
                for (Object e : expList) {
                    if (e instanceof Map<?, ?> emap) {
                        Experience ex = new Experience();
                        Object company = emap.get("company");
                        Object role = emap.get("role");
                        Object year = emap.get("year");
                        Object duration = emap.get("duration");
                        Object competences = emap.get("competences");
                        if (company != null)
                            ex.setCompany(company.toString());
                        if (role != null)
                            ex.setRole(role.toString());
                        if (year != null)
                            ex.setYear(year.toString());
                        if (duration != null)
                            ex.setDuration(duration.toString());
                        if (competences instanceof List<?> compList) {
                            List<String> comps = new ArrayList<>();
                            for (Object c : compList)
                                if (c != null)
                                    comps.add(c.toString());
                            ex.setCompetences(comps);
                        }
                        expEntities.add(ex);
                    }
                }
            }
            cvanalyse.setExperiences(expEntities);

            // 🟦 Persist in DB
            Cvanalyse saved = cvanalyseRepository.save(cvanalyse);

            // 🟦 Return object with saved id and analysis
            Map<String, Object> result = Map.of(
                    "id", saved.getId(),
                    "analysis", parsedJson);

            return result;

        } catch (Exception e) {
            System.err.println("[CVService] Erreur IA ou mapping: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'analyse IA: " + e.getMessage(), e);
        }
    }

    private String extractJsonFromResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return "{}"; // Fallback JSON vide
        }

        String cleaned = response.trim();

        Pattern codeBlockPattern = Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)```\\s*", Pattern.DOTALL);
        var matcher = codeBlockPattern.matcher(cleaned);
        if (matcher.find()) {
            cleaned = matcher.group(1).trim();
        }

        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}') + 1;
        if (start >= 0 && end > start) {
            cleaned = cleaned.substring(start, end).trim();
        }

        cleaned = cleaned.replaceAll("/\\*.*?\\*/", "").replaceAll("//.*", "").trim();

        return cleaned.isEmpty() ? "{}" : cleaned;
    }

    /**
     * Récupère tous les CV analysés d'un utilisateur
     */
    public List<Cvanalyse> getCvsByUserId(Long userId) {
        return cvanalyseRepository.findAllByUser_Id(userId);
    }

    /**
     * Récupère un CV par son ID
     */
    public Cvanalyse getCvById(Long id) {
        return cvanalyseRepository.getCvanalyseById(id);
    }
}