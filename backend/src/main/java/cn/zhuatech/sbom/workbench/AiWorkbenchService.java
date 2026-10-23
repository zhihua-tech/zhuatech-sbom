/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.sbom.workbench;

import static cn.zhuatech.sbom.workbench.WorkbenchInput.*;

import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Map<String, Object> execute(Map<String, Object> r) {
    validate(r);

    keys(r, "roots", "components", "advisories");
    Map<String, List<String>> graph = new TreeMap<>();
    for (var c : rows(r, "components", 1, 200)) {
      keys(c, "id", "dependsOn");
      String id = id(c, "id");
      var deps = strings(c, "dependsOn", 0, 100);
      if (new HashSet<>(deps).size() != deps.size() || graph.putIfAbsent(id, deps) != null)
        throw new IllegalArgumentException("组件或依赖重复");
    }
    for (var deps : graph.values())
      if (!graph.keySet().containsAll(deps)) throw new IllegalArgumentException("未知组件依赖");
    var roots = new TreeSet<>(strings(r, "roots", 1, 100));
    if (!graph.keySet().containsAll(roots)) throw new IllegalArgumentException("未知应用根");
    Map<String, List<String>> paths = new HashMap<>();
    Deque<String> queue = new ArrayDeque<>();
    for (String id : roots) {
      paths.put(id, List.of(id));
      queue.add(id);
    }
    while (!queue.isEmpty()) {
      String id = queue.remove();
      var deps = new TreeSet<>(graph.get(id));
      for (String dep : deps)
        if (!paths.containsKey(dep)) {
          var p = new ArrayList<>(paths.get(id));
          p.add(dep);
          paths.put(dep, List.copyOf(p));
          queue.add(dep);
        }
    }
    Set<String> ids = new HashSet<>();
    List<Map<String, Object>> out = new ArrayList<>();
    int affected = 0;
    for (var a : rows(r, "advisories", 0, 300)) {
      keys(a, "id", "component", "severity");
      String id = id(a, "id");
      unique(ids, id);
      String component = id(a, "component"), severity = text(a, "severity", 10);
      if (!Set.of("LOW", "MEDIUM", "HIGH", "CRITICAL", "UNKNOWN").contains(severity))
        throw new IllegalArgumentException("严重级别错误");
      String state =
          !graph.containsKey(component)
              ? "UNKNOWN_COMPONENT"
              : paths.containsKey(component) ? "REACHABLE" : "NOT_REACHABLE";
      if (state.equals("REACHABLE")) affected++;
      out.add(
          Map.of(
              "id",
              id,
              "component",
              component,
              "severity",
              severity,
              "state",
              state,
              "path",
              paths.getOrDefault(component, List.of())));
    }
    return Map.of(
        "reachableAdvisories", affected, "advisories", out, "runtimeExploitabilityProven", false);
  }
}
